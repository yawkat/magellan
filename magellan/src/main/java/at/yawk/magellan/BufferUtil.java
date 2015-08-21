package at.yawk.magellan;

import at.yawk.magellan.internal.GrowingBuffer;
import at.yawk.rjoin.zlib.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;

/**
 * @author yawkat
 */
class BufferUtil {
    /**
     * Process the given input through the given stream.
     *
     * @param frontPadding How much padding the output buffer should have at the start.
     * @return The full processed output.
     */
    static ByteBuffer deflateInflate(ZStream stream, ByteBuffer input, int frontPadding) throws ZlibException {
        if (Zlib.supportsNative() && !input.isDirect()) {
            ByteBuffer direct = ByteBuffer.allocateDirect(input.remaining());
            direct.put(input);
            input = direct;
            input.flip();
        }

        stream.setInput(input);
        if (stream instanceof ZDeflater) {
            ((ZDeflater) stream).finish();
        }

        GrowingBuffer outBuffer = new GrowingBuffer();

        int noneMovedCount = 0;
        while (!stream.finished()) {
            ByteBuffer partBuffer = outBuffer.nextWritable(4096);
            if (stream instanceof ZInflater) {
                ((ZInflater) stream).inflate(partBuffer);
            } else {
                assert stream instanceof ZDeflater;
                ((ZDeflater) stream).deflate(partBuffer);
            }
            int moved = partBuffer.position();
            if (moved == 0) {
                if (noneMovedCount++ > 4) {
                    throw new ZlibException("No data moved");
                }
            } else {
                noneMovedCount = 0;
            }
        }

        assert !input.hasRemaining();

        return outBuffer.combine(frontPadding);
    }

    public static void print(ByteBuffer buffer) {
        System.out.println("--------------");
        ByteBuffer slice = buffer.slice();
        byte[] array = new byte[32];
        while (slice.remaining() > 0) {
            int n = Math.min(array.length, slice.remaining());
            slice.get(array, 0, n);
            byte[] print = n < array.length ? Arrays.copyOf(array, n) : array;
            System.out.println(DatatypeConverter.printHexBinary(print).toLowerCase());
        }
    }
}
