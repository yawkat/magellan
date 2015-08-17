package at.yawk.magellan.internal;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yawkat
 */
public class GrowingBuffer {
    private final List<ByteBuffer> buffers = new ArrayList<>();

    public ByteBuffer nextWritable(int size) {
        ByteBuffer partBuffer = ByteBuffer.allocateDirect(size);
        buffers.add(partBuffer);
        return partBuffer;
    }

    public ByteBuffer combine() {
        return combine(0);
    }

    public ByteBuffer combine(int frontPadding) {
        int totalSize = buffers.stream().mapToInt(Buffer::position).sum();
        ByteBuffer sumBuffer = ByteBuffer.allocateDirect(totalSize + frontPadding);
        sumBuffer.position(frontPadding);
        for (ByteBuffer buffer : buffers) {
            buffer.flip();
            sumBuffer.put(buffer);
        }
        sumBuffer.flip();
        return sumBuffer;
    }
}
