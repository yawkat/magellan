package at.yawk.magellan;

import at.yawk.magellan.nbt.Tag;
import lombok.Getter;

/**
 * @author yawkat
 */
public class Section {
    @Getter
    private final Tag tag;

    Section(Tag tag) {
        this.tag = tag;
    }

    public byte getYIndex() {
        return tag.getTag("Y").byteValue();
    }
}
