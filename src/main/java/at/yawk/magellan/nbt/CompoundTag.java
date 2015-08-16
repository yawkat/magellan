package at.yawk.magellan.nbt;

import java.util.Map;

/**
 * @author yawkat
 */
class CompoundTag extends Tag {
    private final Map<String, Tag> tags;

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
}
