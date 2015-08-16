package at.yawk.magellan.nbt;

import java.util.List;
import javax.annotation.Nullable;

/**
 * @author yawkat
 */
class ListTag extends Tag {
    @Nullable
    private TagType componentType;
    private final List<Tag> tags;

    ListTag(@Nullable TagType componentType, List<Tag> tags) {
        super(TagType.LIST);
        this.componentType = componentType;
        this.tags = tags;
    }

    @Override
    public boolean isList() {
        return true;
    }

    @Override
    public Tag getTag(int i) {
        return tags.get(i);
    }

    @Override
    public void setTag(int i, Tag value) {
        if (value.getType() != componentType) {
            throw new IllegalArgumentException("Incompatible types " + value.getType() + " and " + componentType);
        }
        tags.set(i, value);
    }

    @Override
    public void removeTag(int i) {
        tags.remove(i);
        if (tags.isEmpty()) {
            componentType = null;
        }
    }

    @Override
    public void addTag(Tag tag) {
        if (componentType != null) {
            if (tag.getType() != componentType) {
                throw new IllegalArgumentException("Incompatible types " + tag.getType() + " and " + componentType);
            }
        } else {
            componentType = tag.getType();
        }
        tags.add(tag);
    }

    @Override
    public int size() {
        return tags.size();
    }
}
