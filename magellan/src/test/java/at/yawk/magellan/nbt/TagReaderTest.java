package at.yawk.magellan.nbt;

import at.yawk.magellan.nbt.lexer.Lexer;
import java.nio.ByteBuffer;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author yawkat
 */
public class TagReaderTest {
    @Test
    public void testSmall() throws Exception {
        ByteBuffer input = NbtTest.small();

        Lexer lexer = Lexer.create();
        lexer.setInput(input, true);

        TagReader parser = TagReader.create(lexer);
        RootTag rootTag = parser.parse();

        assertEquals(input.remaining(), 0);
        assertEquals(rootTag, NbtTest.smallTag);
    }

    @Test
    public void testBig() throws Exception {
        ByteBuffer input = NbtTest.big();

        Lexer lexer = Lexer.create();
        lexer.setInput(input, true);

        TagReader parser = TagReader.create(lexer);
        RootTag rootTag = parser.parse();

        assertEquals(input.remaining(), 0);
        assertEquals(rootTag, NbtTest.bigTag);
    }
}