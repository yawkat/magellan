package at.yawk.magellan.nbt;

import java.nio.IntBuffer;

/**
 * @author yawkat
 */
class IntArrayTag extends Tag {
    private final IntBuffer array;

    IntArrayTag(IntBuffer array) {
        super(TagType.INT_ARRAY);
        this.array = array;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public int getInt(int i) {
        return array.get(i);
    }

    @Override
    public byte getByte(int i) {
        return (byte) getInt(i);
    }

    @Override
    public void setInt(int i, int value) {
        array.put(i, value);
    }

    @Override
    public void setByte(int i, byte value) {
        setInt(i, value & 0xff);
    }

    @Override
    public int size() {
        return array.capacity();
    }
}
