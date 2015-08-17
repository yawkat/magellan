package at.yawk.magellan.nbt.lexer;

/**
 * @author yawkat
 */
interface TagId {
    byte END = 0;

    byte BYTE = 1;
    byte SHORT = 2;
    byte INT = 3;
    byte LONG = 4;
    byte FLOAT = 5;
    byte DOUBLE = 6;
    byte STRING = 8;

    byte BYTE_ARRAY = 7;
    byte INT_ARRAY = 11;

    byte LIST = 9;
    byte COMPOUND = 10;
}
