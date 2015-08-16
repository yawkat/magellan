package at.yawk.magellan.nbt;

/**
 * @author yawkat
 */
class StringTag extends Tag {
    private final CharSequence value;

    StringTag(TagType type, CharSequence value) {
        super(type);
        this.value = value;
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public String stringValue() {
        return value.toString();
    }
}
