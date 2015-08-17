package at.yawk.magellan.nbt;

import java.util.Iterator;
import java.util.Map;
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
    public Iterator<Tag> iterator() {
        return tags.values().iterator();
    }

}
