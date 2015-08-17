package at.yawk.magellan.nbt;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author yawkat
 */
public class PrinterTest {
    @Test
    public void testSmall() {
        assertEquals(Printer.compact().print(NbtTest.smallTag.getTag()),
                     "Compound{\"name\"=string(\"Bananrama\")}");
        assertEquals(Printer.pretty().print(NbtTest.smallTag.getTag()),
                     "Compound {\n" +
                     "  name = \"Bananrama\"\n" +
                     "}");
    }

    @Test
    public void testBig() {
        assertEquals(Printer.compact().print(NbtTest.bigTag.getTag()),
                     "Compound{\"longTest\"=long(9223372036854775807), \"shortTest\"=short(32767), " +
                     "\"stringTest\"=string(\"HELLO WORLD THIS IS A TEST STRING ÅÄÖ!\"), \"floatTest\"=float(0" +
                     ".4982314705848694), \"intTest\"=int(2147483647), \"nested compound " +
                     "test\"=Compound{\"ham\"=Compound{\"name\"=string(\"Hampus\"), \"value\"=float(0.75)}, " +
                     "\"egg\"=Compound{\"name\"=string(\"Eggbert\"), \"value\"=float(0.5)}}, \"listTest (long)" +
                     "\"=List[long(11), long(12), long(13), long(14), long(15)], \"listTest (compound)" +
                     "\"=List[Compound{\"name\"=string(\"Compound tag #0\"), \"created-on\"=long(1264099775885)}, " +
                     "Compound{\"name\"=string(\"Compound tag #1\"), \"created-on\"=long(1264099775885)}], " +
                     "\"byteTest\"=byte(127), \"byteArrayTest (the first 1000 values of (n*n*255+n*7)%100, starting " +
                     "with n=0 (0, 62, 34, 16, 8, ...))\"=IntArray[length=1000], \"doubleTest\"=double(0" +
                     ".4931287132182315)}");
        assertEquals(Printer.pretty().print(NbtTest.bigTag.getTag()),
                     "Compound {\n" +
                     "  longTest = 9223372036854775807, \n" +
                     "  shortTest = 32767, \n" +
                     "  stringTest = \"HELLO WORLD THIS IS A TEST STRING ÅÄÖ!\", \n" +
                     "  floatTest = 0.4982314705848694, \n" +
                     "  intTest = 2147483647, \n" +
                     "  nested compound test = Compound {\n" +
                     "    ham = Compound {\n" +
                     "      name = \"Hampus\", \n" +
                     "      value = 0.75\n" +
                     "    }, \n" +
                     "    egg = Compound {\n" +
                     "      name = \"Eggbert\", \n" +
                     "      value = 0.5\n" +
                     "    }\n" +
                     "  }, \n" +
                     "  listTest (long) = List [\n" +
                     "    11, 12, 13, 14, \n" +
                     "    15\n" +
                     "  ], \n" +
                     "  listTest (compound) = List [\n" +
                     "    Compound {\n" +
                     "      name = \"Compound tag #0\", \n" +
                     "      created-on = 1264099775885\n" +
                     "    }, \n" +
                     "    Compound {\n" +
                     "      name = \"Compound tag #1\", \n" +
                     "      created-on = 1264099775885\n" +
                     "    }\n" +
                     "  ], \n" +
                     "  byteTest = 127, \n" +
                     "  byteArrayTest (the first 1000 values of (n*n*255+n*7)%100, starting with n=0 (0, 62, 34, 16, " +
                     "8, ...)) = IntArray[length=1000], \n" +
                     "  doubleTest = 0.4931287132182315\n" +
                     "}");
    }
}