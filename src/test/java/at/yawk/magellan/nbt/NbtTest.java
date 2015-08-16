package at.yawk.magellan.nbt;

import at.yawk.magellan.Util;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author yawkat
 */
public class NbtTest {
    public static ByteBuffer small() throws IOException {
        return Util.loadResource(NbtTest.class.getResource("small.nbt"));
    }

    public static ByteBuffer big() throws IOException {
        return Util.loadResource(NbtTest.class.getResource("big.nbt"));
    }
}
