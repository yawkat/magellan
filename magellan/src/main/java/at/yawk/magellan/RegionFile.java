package at.yawk.magellan;

import at.yawk.rjoin.zlib.ZlibException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author yawkat
 */
public class RegionFile {
    private final ByteBuffer buffer;

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

    public ByteBuffer getChunkBytes(ChunkPosition position) {
        ByteBuffer blocks = buffer.duplicate();
        int start = position.getOffset() * 4096;
        blocks.position(start);
        int length = blocks.getInt();
        blocks.limit(blocks.position() + length);
        return blocks.slice();
    }

    public Chunk getChunk(ChunkPosition position) throws ZlibException {
        return Chunk.fromCompressedBuffer(getChunkBytes(position));
    }
}
