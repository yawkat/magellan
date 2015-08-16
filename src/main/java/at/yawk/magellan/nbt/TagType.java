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
    INT_ARRAY,
}
