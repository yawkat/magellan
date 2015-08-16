package at.yawk.magellan.nbt;

/**
 * @author yawkat
 */
class FloatTag extends Tag {
    private final double value;

    FloatTag(TagType type, double value) {
        super(type);
        this.value = value;
    }

    @Override
    public boolean isFloat() {
        return true;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public long longValue() {
        return (long) value;
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