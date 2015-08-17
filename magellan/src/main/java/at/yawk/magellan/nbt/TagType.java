package at.yawk.magellan.nbt;

import lombok.Getter;

/**
 * @author yawkat
 */
@Getter
public enum TagType {
    BYTE,
    SHORT,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    BYTE_ARRAY,
    STRING,
    LIST,
    COMPOUND,
    INT_ARRAY;

    public boolean isInteger() {
        return this == BYTE | this == SHORT |
               this == INT | this == LONG;
    }

    public boolean isFloat() {
        return this == FLOAT | this == DOUBLE;
    }

    public boolean isNumber() {
        return isInteger() || isFloat();
    }

    public boolean isArray() {
        return this == BYTE_ARRAY | this == INT_ARRAY;
    }

    public boolean isComposite() {
        return this == LIST | this == COMPOUND;
    }
}
