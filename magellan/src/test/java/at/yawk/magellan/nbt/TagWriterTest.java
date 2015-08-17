package at.yawk.magellan.nbt;

import at.yawk.magellan.nbt.lexer.Emitter;
import at.yawk.magellan.nbt.lexer.Lexer;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author yawkat
 */
public class TagWriterTest {
    // run twice to confirm we didn't modify NbtTest.smallTag
    @Test(invocationCount = 2)
    public void testSmall() throws IOException {
        ByteBuffer expected = NbtTest.small();
        ByteBuffer output = ByteBuffer.allocate(expected.remaining());
        Emitter emitter = Emitter.create();
        emitter.setOutput(output);
        TagWriter.create(emitter, NbtTest.smallTag).emit();
        output.flip();

        Assert.assertEquals(output, expected);
    }

    // run twice to confirm we didn't modify NbtTest.bigTag
    @Test(invocationCount = 2)
    public void testBig() throws IOException {
        ByteBuffer expected = NbtTest.big();
        ByteBuffer output = ByteBuffer.allocate(expected.remaining());
        Emitter emitter = Emitter.create();
        emitter.setOutput(output);
        TagWriter.create(emitter, NbtTest.bigTag).emit();
        output.flip();

        Assert.assertEquals(output, expected);
    }

    // test emitting lazily loaded strings
    @Test
    public void testLazyEmit() throws IOException {
        ByteBuffer expected = NbtTest.big();
        ByteBuffer actual = ByteBuffer.allocateDirect(expected.capacity());

        Lexer lexer = Lexer.create();
        lexer.setInput(expected, true);
        RootTag tag = TagReader.create(lexer).parse();

        Emitter emitter = Emitter.create();
        emitter.setOutput(actual);
        TagWriter.create(emitter, tag).emit();

        expected.flip();
        actual.flip();

        Assert.assertEquals(expected, actual);
    }
}