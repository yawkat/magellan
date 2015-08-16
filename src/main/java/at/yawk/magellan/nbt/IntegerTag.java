package at.yawk.magellan.nbt;

/**
 * @author yawkat
 */
class IntegerTag extends Tag {
    private final long value;

    IntegerTag(TagType type, long value) {
        super(type);
        this.value = value;
    }

    @Override
    public boolean isInteger() {
        return true;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public String stringValue() {
        return String.valueOf(value);
    }
}
