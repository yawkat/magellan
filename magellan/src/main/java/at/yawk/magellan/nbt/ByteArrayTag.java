package at.yawk.magellan.nbt;

import java.nio.ByteBuffer;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author yawkat
 */
@EqualsAndHashCode(of = "array", callSuper = false)
class ByteArrayTag extends Tag {
    @Getter private final ByteBuffer array;

    ByteArrayTag(ByteBuffer array) {
        super(TagType.BYTE_ARRAY);
        this.array = array;

        array.position(0);
        array.limit(array.capacity());
    }

    @Override
    public byte getByte(int i) {
        return array.get(i);
    }

    @Override
    public int getInt(int i) {
        return getByte(i) & 0xff;
    }

    @Override
    public void setByte(int i, byte value) {
        array.put(i, value);
    }

    @Override
    public void setInt(int i, int value) {
        setByte(i, (byte) value);
    }

    @Override
    public int size() {
        return array.capacity();
    }

}
