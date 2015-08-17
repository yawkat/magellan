package at.yawk.magellan.nbt.lexer;

/**
 * @author yawkat
 */
public enum Event {
    PRIMITIVE,

    START_LIST,
    END_LIST,
    START_COMPOUND,
    END_COMPOUND,
}
