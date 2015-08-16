package at.yawk.magellan;

import at.yawk.rjoin.zlib.ZInflater;
import at.yawk.rjoin.zlib.Zlib;
import at.yawk.rjoin.zlib.ZlibException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yawkat
 */
public class Chunk {
    private final ByteBuffer buffer;

    private Chunk(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public static Chunk fromBuffer(ByteBuffer buffer) {
        return new Chunk(buffer);
    }

    public static Chunk fromCompressedBuffer(ByteBuffer buffer) throws ZlibException {
        byte compressionType = buffer.get();
        switch (compressionType) {
        case 2: // zlib
            if (Zlib.supportsNative() && !buffer.isDirect()) {
                ByteBuffer direct = ByteBuffer.allocateDirect(buffer.remaining());
                direct.put(buffer);
                buffer = direct;
                buffer.flip();
            }

            try (ZInflater inflater = Zlib.getProvider().createInflater()) {
                inflater.setInput(buffer);

                int totalSize = 0;
                List<ByteBuffer> outBuffers = new ArrayList<>();
                while (!inflater.finished()) {
                    ByteBuffer partBuffer = ByteBuffer.allocateDirect(4096);
                    inflater.inflate(partBuffer);
                    outBuffers.add(partBuffer);
                    totalSize = Math.addExact(totalSize, partBuffer.position());
                }

                ByteBuffer sumBuffer = ByteBuffer.allocateDirect(totalSize);
                for (ByteBuffer component : outBuffers) {
                    component.flip();
                    sumBuffer.put(component);
                }
                return new Chunk(sumBuffer);
            }
        default:
            throw new UnsupportedOperationException("Unsupported compression type " + compressionType);
        }
    }
}
