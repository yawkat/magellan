package at.yawk.magellan.nbt;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;

/**
 * @author yawkat
 */
@EqualsAndHashCode(of = "type")
public abstract class Tag implements Iterable<Tag> {
    private final TagType type;

    Tag(TagType type) {
        this.type = type;
    }

    public final TagType getType() {
        return type;
    }

    @Override
    public final String toString() {
        return Printer.compact().print(this);
    }

    public final String toStringPretty() {
        return Printer.pretty().print(this);
    }

    /// TYPE ACCESSORS

    public final boolean isString() {
        return type == TagType.STRING;
    }

    public final boolean isInteger() {
        return type.isInteger();
    }

    public final boolean isFloat() {
        return type.isFloat();
    }

    public final boolean isNumber() {
        return type.isNumber();
    }

    public final boolean isArray() {
        return type.isArray();
    }

    public final boolean isList() {
        return type == TagType.LIST;
    }

    public final boolean isCompound() {
        return type == TagType.COMPOUND;
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

    @Override
    public Iterator<Tag> iterator() {
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
        return new StringTag(value);
    }

    public static Tag ofList(TagType componentType, List<Tag> tags) {
        tags.forEach(t -> {
            if (t.getType() != componentType) {
                throw new IllegalArgumentException(
                        "Mismatched component type (expected " + componentType + ", was " + t.getType() + ")");
            }
        });
        return new ListTag(componentType, tags);
    }

    public static Tag ofMap(Map<String, Tag> tags) {
        return new CompoundTag(tags);
    }

    /**
     * Get a {@link TagType#BYTE_ARRAY} tag from readable bytes of the given buffer. The buffer positions will not be
     * modified. The returned tag will share the buffer.
     */
    public static Tag ofByteBuffer(ByteBuffer buffer) {
        return new ByteArrayTag(buffer.slice());
    }

    public static Tag ofByteArray(byte[] array) {
        return new ByteArrayTag(ByteBuffer.wrap(array));
    }

    /**
     * Get a {@link TagType#INT_ARRAY} tag from readable ints of the given buffer. The buffer positions will not be
     * modified. The returned tag will share the buffer.
     */
    public static Tag ofIntBuffer(IntBuffer buffer) {
        return new IntArrayTag(buffer.slice());
    }

    public static Tag ofIntArray(int[] array) {
        return new IntArrayTag(IntBuffer.wrap(array));
    }

    private UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException(getClass().getName());
    }
}
