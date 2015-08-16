package at.yawk.magellan.nbt.lexer;

/**
 * @author yawkat
 */
interface Walker {
    Event next() throws NeedInputException;
}
