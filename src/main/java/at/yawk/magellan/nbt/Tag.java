package at.yawk.magellan.nbt;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

/**
 * @author yawkat
 */
@RequiredArgsConstructor
public abstract class Tag {
    private final TagType type;

    public final TagType getType() {
        return type;
    }

    /// TYPE ACCESSORS

    public boolean isString() {
        return false;
    }

    public boolean isInteger() {
        return false;
    }

    public boolean isFloat() {
        return false;
    }

    public boolean isNumber() {
        return false;
    }

    public boolean isArray() {
        return false;
    }

    public boolean isList() {
        return false;
    }

    public boolean isCompound() {
        return false;
    }

    /// PRIMITIVE ACCESSORS

    public byte byteValue() {
        return (byte) longValue();
    }

    public short shortValue() {
        return (short) longValue();
    }

    public int intValue() {
        return (int) longValue();
    }

    public long longValue() {
        throw unsupported();
    }

    public double doubleValue() {
        throw unsupported();
    }

    public String stringValue() {
        throw unsupported();
    }

    /// COMPOUND ACCESSORS

    public Tag getTag(int i) {
        throw unsupported();
    }

    public void setTag(int i, Tag value) {
        throw unsupported();
    }

    public void removeTag(int i) {
        throw unsupported();
    }

    public void addTag(Tag tag) {
        throw unsupported();
    }

    public Tag getTag(String key) {
        throw unsupported();
    }

    public void setTag(String key, Tag value) {
        throw unsupported();
    }

    public void removeTag(String key) {
        throw unsupported();
    }

    public byte getByte(int i) {
        throw unsupported();
    }

    public void setByte(int i, byte value) {
        throw unsupported();
    }

    public int getInt(int i) {
        throw unsupported();
    }

    public void setInt(int i, int value) {
        throw unsupported();
    }

    public int size() {
        throw unsupported();
    }

    /// FACTORIES

    public static Tag ofByte(byte b) {
        return new IntegerTag(TagType.BYTE, b & 0xffL);
    }

    public static Tag ofShort(short s) {
        return new IntegerTag(TagType.SHORT, s & 0xffffL);
    }

    public static Tag ofInt(int i) {
        return new IntegerTag(TagType.INT, i & 0xffffffffL);
    }

    public static Tag ofLong(long l) {
        return new IntegerTag(TagType.LONG, l);
    }

    public static Tag ofFloat(float f) {
        return new FloatTag(TagType.FLOAT, f);
    }

    public static Tag ofDouble(double d) {
        return new FloatTag(TagType.DOUBLE, d);
    }

    public static Tag ofString(String value) {
        return new StringTag(TagType.STRING, value);
    }

    public static Tag ofList(TagType componentType, List<Tag> tags) {
        return new ListTag(componentType, tags);
    }

    public static Tag ofMap(Map<String, Tag> tags) {
        return new CompoundTag(tags);
    }

    public static Tag ofArray(byte[] array) {
        return new ByteArrayTag(ByteBuffer.wrap(array));
    }

    public static Tag ofArray(int[] array) {
        return new IntArrayTag(IntBuffer.wrap(array));
    }

    private UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException(getClass().getName());
    }
}
