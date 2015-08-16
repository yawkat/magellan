package at.yawk.magellan.nbt.lexer;

import at.yawk.magellan.nbt.NbtTest;
import at.yawk.magellan.nbt.TagType;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author yawkat
 */
public class LexerTest {
    @Test
    public void testSmall() throws IOException {
        ByteBuffer input = NbtTest.small();
        Lexer lexer = Lexer.create();
        lexer.setInput(input, true);

        assertEquals(lexer.next(), Event.START_COMPOUND);
        assertEquals(lexer.getName().toString(), "hello world");

        assertEquals(lexer.next(), Event.PRIMITIVE);
        assertEquals(lexer.getType(), TagType.STRING);
        assertEquals(lexer.getStringValue().toString(), "Bananrama");

        assertEquals(lexer.next(), Event.END_COMPOUND);

        assertNull(lexer.next());
        assertEquals(input.remaining(), 0);
    }

    @Test
    public void testSmallCopy() throws IOException {
        ByteBuffer input = NbtTest.small();
        Lexer lexer = Lexer.create();
        lexer.setInput(input, false);

        assertEquals(lexer.next(), Event.START_COMPOUND);
        assertEquals(lexer.getName().toString(), "hello world");

        assertEquals(lexer.next(), Event.PRIMITIVE);
        assertEquals(lexer.getType(), TagType.STRING);
        CharSequence string = lexer.getStringValue();

        assertEquals(lexer.next(), Event.END_COMPOUND);

        assertNull(lexer.next());
        assertEquals(input.remaining(), 0);

        // clear buffer to test if we copied the data
        input.clear();
        while (input.hasRemaining()) { input.put((byte) 0); }

        assertEquals(string.toString(), "Bananrama");
    }

    @Test
    public void testSmallAsync() throws IOException {
        ByteBuffer input = NbtTest.small();

        input.limit(input.capacity() / 2);
        ByteBuffer firstSlice = input.slice();
        input.limit(input.capacity());

        Lexer lexer = Lexer.create();
        lexer.setInput(firstSlice, true);

        assertEquals(lexer.next(), Event.START_COMPOUND);
        assertEquals(lexer.getName().toString(), "hello world");

        try {
            assertEquals(lexer.next(), Event.PRIMITIVE);
            Assert.fail(); // need exception
        } catch (NeedInputException e) {
            input.position(firstSlice.position());
            lexer.setInput(input, true);
            assertEquals(lexer.next(), Event.PRIMITIVE);
        }

        assertEquals(lexer.getType(), TagType.STRING);
        assertEquals(lexer.getStringValue().toString(), "Bananrama");

        assertEquals(lexer.next(), Event.END_COMPOUND);

        assertNull(lexer.next());
        assertEquals(input.remaining(), 0);
    }

    @Test
    public void testBig() throws IOException {
        ByteBuffer input = NbtTest.big();
        Lexer lexer = Lexer.create();
        lexer.setInput(input, true);

        assertEquals(lexer.next(), Event.START_COMPOUND);
        assertEquals(lexer.getName().toString(), "Level");

        assertEquals(lexer.next(), Event.PRIMITIVE);
        assertEquals(lexer.getType(), TagType.LONG);
        assertEquals(lexer.getName().toString(), "longTest");
        assertEquals(lexer.getLongValue(), 9223372036854775807L);

        assertEquals(lexer.next(), Event.PRIMITIVE);
        assertEquals(lexer.getType(), TagType.SHORT);
        assertEquals(lexer.getName().toString(), "shortTest");
        assertEquals(lexer.getLongValue(), 32767);

        assertEquals(lexer.next(), Event.PRIMITIVE);
        assertEquals(lexer.getType(), TagType.STRING);
        assertEquals(lexer.getName().toString(), "stringTest");
        assertEquals(lexer.getStringValue().toString(), "HELLO WORLD THIS IS A TEST STRING ÅÄÖ!");

        assertEquals(lexer.next(), Event.PRIMITIVE);
        assertEquals(lexer.getType(), TagType.FLOAT);
        assertEquals(lexer.getName().toString(), "floatTest");
        assertEquals(lexer.getDoubleValue(), (double) 0.49823147058486938F);

        assertEquals(lexer.next(), Event.PRIMITIVE);
        assertEquals(lexer.getType(), TagType.INT);
        assertEquals(lexer.getName().toString(), "intTest");
        assertEquals(lexer.getLongValue(), 2147483647);

        assertEquals(lexer.next(), Event.START_COMPOUND);
        assertEquals(lexer.getName().toString(), "nested compound test");

        assertEquals(lexer.next(), Event.START_COMPOUND);
        assertEquals(lexer.getName().toString(), "ham");

        assertEquals(lexer.next(), Event.PRIMITIVE);
        assertEquals(lexer.getType(), TagType.STRING);
        assertEquals(lexer.getName().toString(), "name");
        assertEquals(lexer.getStringValue().toString(), "Hampus");

        assertEquals(lexer.next(), Event.PRIMITIVE);
        assertEquals(lexer.getType(), TagType.FLOAT);
        assertEquals(lexer.getName().toString(), "value");
        assertEquals(lexer.getDoubleValue(), 0.75);

        assertEquals(lexer.next(), Event.END_COMPOUND);

        assertEquals(lexer.next(), Event.START_COMPOUND);
        assertEquals(lexer.getName().toString(), "egg");

        assertEquals(lexer.next(), Event.PRIMITIVE);
        assertEquals(lexer.getType(), TagType.STRING);
        assertEquals(lexer.getName().toString(), "name");
        assertEquals(lexer.getStringValue().toString(), "Eggbert");

        assertEquals(lexer.next(), Event.PRIMITIVE);
        assertEquals(lexer.getType(), TagType.FLOAT);
        assertEquals(lexer.getName().toString(), "value");
        assertEquals(lexer.getDoubleValue(), 0.5);

        assertEquals(lexer.next(), Event.END_COMPOUND);

        assertEquals(lexer.next(), Event.END_COMPOUND);

        assertEquals(lexer.next(), Event.START_LIST);
        assertEquals(lexer.getName().toString(), "listTest (long)");

        for (int i = 11; i <= 15; i++) {
            assertEquals(lexer.next(), Event.PRIMITIVE);
            assertEquals(lexer.getType(), TagType.LONG);
            assertEquals(lexer.getLongValue(), i);
        }

        assertEquals(lexer.next(), Event.END_LIST);

        assertEquals(lexer.next(), Event.START_LIST);
        assertEquals(lexer.getName().toString(), "listTest (compound)");

        assertEquals(lexer.next(), Event.START_COMPOUND);

        assertEquals(lexer.next(), Event.PRIMITIVE);
        assertEquals(lexer.getType(), TagType.STRING);
        assertEquals(lexer.getName().toString(), "name");
        assertEquals(lexer.getStringValue().toString(), "Compound tag #0");

        assertEquals(lexer.next(), Event.PRIMITIVE);
        assertEquals(lexer.getType(), TagType.LONG);
        assertEquals(lexer.getName().toString(), "created-on");
        assertEquals(lexer.getLongValue(), 1264099775885L);

        assertEquals(lexer.next(), Event.END_COMPOUND);

        assertEquals(lexer.next(), Event.START_COMPOUND);

        assertEquals(lexer.next(), Event.PRIMITIVE);
        assertEquals(lexer.getType(), TagType.STRING);
        assertEquals(lexer.getName().toString(), "name");
        assertEquals(lexer.getStringValue().toString(), "Compound tag #1");

        assertEquals(lexer.next(), Event.PRIMITIVE);
        assertEquals(lexer.getType(), TagType.LONG);
        assertEquals(lexer.getName().toString(), "created-on");
        assertEquals(lexer.getLongValue(), 1264099775885L);

        assertEquals(lexer.next(), Event.END_COMPOUND);

        assertEquals(lexer.next(), Event.END_LIST);

        assertEquals(lexer.next(), Event.PRIMITIVE);
        assertEquals(lexer.getType(), TagType.BYTE);
        assertEquals(lexer.getName().toString(), "byteTest");
        assertEquals(lexer.getLongValue(), 127);

        assertEquals(lexer.next(), Event.PRIMITIVE);
        assertEquals(lexer.getType(), TagType.BYTE_ARRAY);
        assertEquals(lexer.getName().toString(),
                     "byteArrayTest (the first 1000 values of (n*n*255+n*7)%100, starting with n=0 (0, 62, 34, 16, 8," +
                     " ...))");
        ByteBuffer buffer = lexer.getByteArrayValue();
        for (int n = 0; n < 1000; n++) {
            assertEquals(buffer.get(), (byte) ((n * n * 255 + n * 7) % 100));
        }

        assertEquals(lexer.next(), Event.PRIMITIVE);
        assertEquals(lexer.getType(), TagType.DOUBLE);
        assertEquals(lexer.getName().toString(), "doubleTest");
        assertEquals(lexer.getDoubleValue(), 0.49312871321823148);

        assertEquals(lexer.next(), Event.END_COMPOUND);

        assertNull(lexer.next());
        assertEquals(input.remaining(), 0);
    }

    @Test
    public void testWalkerAlloc() {
        // make sure that elementWalker doesn't realloc each time
        Assert.assertSame(Lexer.elementWalker((byte) 4), Lexer.elementWalker((byte) 4));
    }
}