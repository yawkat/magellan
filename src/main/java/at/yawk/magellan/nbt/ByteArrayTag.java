package at.yawk.magellan.nbt;

import java.nio.ByteBuffer;

/**
 * @author yawkat
 */
class ByteArrayTag extends Tag {
    private final ByteBuffer array;

    ByteArrayTag(ByteBuffer array) {
        super(TagType.INT_ARRAY);
        this.array = array;
    }

    @Override
    public boolean isArray() {
        return true;
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
