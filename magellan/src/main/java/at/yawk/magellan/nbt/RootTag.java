package at.yawk.magellan.nbt;

import lombok.Value;

/**
 * @author yawkat
 */
@Value
public class RootTag {
    private final String name;
    private final Tag tag;

    @Override
    public String toString() {
        return "Root{\"" + name + "\"=" + tag + "}";
    }
}
