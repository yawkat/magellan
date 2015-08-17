package at.yawk.magellan;

import at.yawk.rjoin.zlib.ZlibException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yawkat
 */
@Slf4j
@NotThreadSafe
public class RegionFile {
    /**
     * Region buffer. Mark, position and limit are arbitrary.
     */
    private ByteBuffer buffer;

    private RegionFile(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public static RegionFile fromBuffer(ByteBuffer buffer) {
        return new RegionFile(buffer);
    }

    public static RegionFile load(Path source) throws IOException {
        try (SeekableByteChannel channel = Files.newByteChannel(source)) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(Math.toIntExact(channel.size()));
            while (buffer.hasRemaining()) {
                channel.read(buffer);
            }
            return new RegionFile(buffer);
        }
    }

    @Nullable
    public ChunkPosition getPosition(int x, int z) {
        x &= 31;
        z &= 31;
        int initialOffset = 4 * (x + z * 32);
        int locationMark = buffer.getInt(initialOffset);

        if (locationMark == 0) {
            return null;
        }
        return new ChunkPosition(
                (byte) x, (byte) z,
                locationMark >>> 8,
                (byte) (locationMark & 0xff)
        );
    }

    public void updatePosition(@Nonnull ChunkPosition position) {
        updatePosition(position.getRegionX(), position.getRegionZ(), position);
    }

    public void updatePosition(int x, int z, @Nullable ChunkPosition position) {
        x &= 31;
        z &= 31;
        int initialOffset = 4 * (x + z * 32);
        int locationMark = position == null ? 0 :
                (position.getOffset() << 8) | (position.getSectorCount() & 0xff);
        buffer.putInt(initialOffset, locationMark);
    }

    public List<ChunkPosition> getChunkPositions() {
        List<ChunkPosition> positions = new ArrayList<>();
        for (int z = 0; z < 32; z++) {
            for (int x = 0; x < 32; x++) {
                ChunkPosition position = getPosition(x, z);
                if (position != null) {
                    positions.add(position);
                }
            }
        }
        return positions;
    }

    /**
     * Get the chunk bytes at the given position.
     */
    public ByteBuffer getChunkBytes(ChunkPosition position) {
        ByteBuffer blocks = buffer.duplicate();
        blocks.clear(); // discard mark, pos, limit

        int start = position.getOffset() * 4096;
        blocks.position(start);
        int length = blocks.getInt();
        blocks.limit(blocks.position() + length);
        return blocks.slice();
    }

    /**
     * Get the chunk object at the given location.
     */
    public Chunk getChunk(ChunkPosition position) throws ZlibException {
        return Chunk.fromCompressedBuffer(getChunkBytes(position));
    }

    /**
     * Set the chunk bytes at the given location.
     *
     * <b>This method does not grow the file to accommodate the bytes and will fail if that's required!</b>
     */
    public void setChunkBytes(ChunkPosition position, ByteBuffer bytes) {
        if ((getRequiredSectorCount(bytes.remaining()) & 0xff) >
            (position.getSectorCount() & 0xff)) {
            throw new IllegalArgumentException("Not enough sectors");
        }
        int offset = position.getOffset() * 4096;
        if (buffer.limit() < offset + bytes.remaining() ||
            offset < 8192) {
            throw new IllegalArgumentException("Sectors out of range");
        }
        buffer.clear();
        buffer.position(offset);
        buffer.putInt(bytes.remaining());
        buffer.put(bytes);
    }

    /**
     * Find a free chunk position that could be used to save the chunk at the given location.
     */
    public ChunkPosition findFreeChunkPosition(int x, int z, int chunkSize) {
        x &= 31;
        z &= 31;
        byte requiredSectorCount = getRequiredSectorCount(chunkSize);
        List<ChunkPosition> positions = getChunkPositions();
        positions.sort(Comparator.comparingInt(ChunkPosition::getOffset));

        int lastEnd = 2;
        for (ChunkPosition position : positions) {
            if (position.getRegionX() == x &&
                position.getRegionZ() == z) {
                // ignore the current chunk as if it were unused
                continue;
            }

            int start = position.getOffset();
            if (start - lastEnd >= requiredSectorCount) {
                break;
            } else {
                lastEnd = start + (position.getSectorCount() & 0xff);
            }
        }
        return new ChunkPosition((byte) x, (byte) z, lastEnd, requiredSectorCount);
    }

    /**
     * Grow this file to accommodate the chunk at the given position, if necessary.
     */
    public void growToAccommodate(ChunkPosition position) {
        int requiredSize = (position.getOffset() + (position.getSectorCount() & 0xff)) * 4096;
        if (requiredSize > buffer.capacity()) {
            int newSize = buffer.capacity();
            do {
                newSize = Math.multiplyExact(newSize, 2);
            } while (requiredSize > newSize);
            log.debug("Growing region from {} bytes to {} bytes for {}", buffer.capacity(), newSize, position);
            ByteBuffer newBuffer = buffer.isDirect() ?
                    ByteBuffer.allocateDirect(newSize) :
                    ByteBuffer.allocate(newSize);

            buffer.clear();
            newBuffer.put(buffer);
            buffer = newBuffer;
        }
    }

    /**
     * Save the given chunk buffer somewhere in this file.
     */
    public void saveChunkBytes(int x, int z, ByteBuffer chunkBytes) {
        ChunkPosition position = findFreeChunkPosition(x, z, chunkBytes.remaining());
        growToAccommodate(position);
        setChunkBytes(position, chunkBytes);
        updatePosition(position);
    }

    /**
     * Save the given chunk object somewhere in this file.
     */
    public void saveChunk(int x, int z, Chunk chunk) throws ZlibException {
        saveChunkBytes(x, z, chunk.toCompressedBuffer());
    }

    private static byte getRequiredSectorCount(int payloadSize) {
        int bytes = payloadSize + 4; // length header
        int result = (bytes - 1) / 4096 + 1; // /4096, round up
        if (result > 0xff) { throw new IllegalArgumentException("Payload too large: " + payloadSize); }
        return (byte) result;
    }
}
