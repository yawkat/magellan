package at.yawk.magellan.nbt;

import at.yawk.magellan.nbt.lexer.Lexer;
import com.google.common.collect.ImmutableMap;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author yawkat
 */
public class ParserTest {
    @Test
    public void testSmall() throws Exception {
        ByteBuffer input = NbtTest.small();

        Lexer lexer = Lexer.create();
        lexer.setInput(input, true);

        Parser parser = Parser.create(lexer);
        RootTag rootTag = parser.parse();

        assertEquals(input.remaining(), 0);
        assertEquals(rootTag, NbtTest.smallTag);
    }

    @Test
    public void testBig() throws Exception {
        ByteBuffer input = NbtTest.big();

        Lexer lexer = Lexer.create();
        lexer.setInput(input, true);

        Parser parser = Parser.create(lexer);
        RootTag rootTag = parser.parse();

        assertEquals(input.remaining(), 0);
        assertEquals(rootTag, NbtTest.bigTag);
    }
}