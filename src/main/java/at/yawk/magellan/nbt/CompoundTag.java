package at.yawk.magellan.nbt;

import java.util.Map;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author yawkat
 */
@EqualsAndHashCode(of = "tags", callSuper = false)
class CompoundTag extends Tag {
    @Getter private final Map<String, Tag> tags;

    public CompoundTag(Map<String, Tag> tags) {
        super(TagType.COMPOUND);
        this.tags = tags;
    }

    @Override
    public boolean isCompound() {
        return true;
    }

    @Override
    public Tag getTag(String key) {
        return tags.get(key);
    }

    @Override
    public void setTag(String key, Tag value) {
        tags.put(key, value);
    }

    @Override
    public void removeTag(String key) {
        tags.remove(key);
    }

    @Override
    public int size() {
        return tags.size();
    }

    @Override
    public String toString() {
        return tags.entrySet()
                .stream().map(e -> '"' + String.valueOf(e.getKey()) + "\"=" + e.getValue())
                .collect(Collectors.joining(", ", "Compound{", "}"));
    }
}
