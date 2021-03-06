package at.yawk.magellan.nbt;

import java.util.Objects;
import lombok.Getter;

/**
 * @author yawkat
 */
class StringTag extends Tag {
    @Getter private final CharSequence value;

    StringTag(CharSequence value) {
        super(TagType.STRING);
        this.value = value;
    }

    @Override
    public String stringValue() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        StringTag stringTag = (StringTag) o;
        return Objects.equals(stringValue(), stringTag.stringValue());
    }

    @Override
    public int hashCode() {
        return value.toString().hashCode();
    }
}
