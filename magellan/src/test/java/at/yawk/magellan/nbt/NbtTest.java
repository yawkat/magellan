package at.yawk.magellan.nbt;

import at.yawk.magellan.Util;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

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

    public static final RootTag smallTag;
    public static final RootTag bigTag;

    static {
        smallTag = new RootTag(
                "hello world",
                Tag.ofMap(ImmutableMap.of(
                        "name",
                        Tag.ofString("Bananrama")
                ))
        );

        ByteBuffer testBuffer = ByteBuffer.allocate(1000);
        for (int n = 0; n < 1000; n++) {
            testBuffer.put((byte) ((n * n * 255 + n * 7) % 100));
        }
        testBuffer.flip();

        bigTag = new RootTag(
                "Level",
                Tag.ofMap(ImmutableMap.<String, Tag>builder().put(
                                  "longTest",
                                  Tag.ofLong(9223372036854775807L)
                          ).put(
                                  "shortTest",
                                  Tag.ofShort((short) 32767)
                          ).put(
                                  "stringTest",
                                  Tag.ofString("HELLO WORLD THIS IS A TEST STRING ÅÄÖ!")
                          ).put(
                                  "floatTest",
                                  Tag.ofFloat(0.4982314705848694F)
                          ).put(
                                  "intTest",
                                  Tag.ofInt(2147483647)
                          ).put(
                                  "nested compound test",
                                  Tag.ofMap(ImmutableMap.of(
                                          "ham",
                                          Tag.ofMap(ImmutableMap.of(
                                                  "name",
                                                  Tag.ofString("Hampus"),
                                                  "value",
                                                  Tag.ofFloat(0.75F)
                                          )),
                                          "egg",
                                          Tag.ofMap(ImmutableMap.of(
                                                  "name",
                                                  Tag.ofString("Eggbert"),
                                                  "value",
                                                  Tag.ofFloat(0.5F)
                                          ))
                                  ))
                          ).put(
                                  "listTest (long)",
                                  Tag.ofList(
                                          TagType.LONG,
                                          Arrays.asList(
                                                  Tag.ofLong(11),
                                                  Tag.ofLong(12),
                                                  Tag.ofLong(13),
                                                  Tag.ofLong(14),
                                                  Tag.ofLong(15)
                                          )
                                  )
                          ).put(
                                  "listTest (compound)",
                                  Tag.ofList(
                                          TagType.COMPOUND,
                                          Arrays.asList(
                                                  Tag.ofMap(ImmutableMap.of(
                                                          "name",
                                                          Tag.ofString("Compound tag #0"),
                                                          "created-on",
                                                          Tag.ofLong(1264099775885L)
                                                  )),
                                                  Tag.ofMap(ImmutableMap.of(
                                                          "name",
                                                          Tag.ofString("Compound tag #1"),
                                                          "created-on",
                                                          Tag.ofLong(1264099775885L)
                                                  ))
                                          )
                                  )
                          ).put(
                                  "byteTest",
                                  Tag.ofByte((byte) 127)
                          ).put(
                                  "byteArrayTest (the first 1000 values of (n*n*255+n*7)%100, starting with n=0 (0, " +
                                  "62, 34, 16, 8, ...))",
                                  Tag.ofByteBuffer(testBuffer)
                          ).put(
                                  "doubleTest",
                                  Tag.ofDouble(0.4931287132182315)
                          ).build()
                )
        );
    }
}
