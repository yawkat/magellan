package at.yawk.magellan;

import at.yawk.magellan.nbt.Tag;
import lombok.Getter;

/**
 * @author yawkat
 */
public class TileEntity {
    @Getter
    private final Tag tag;

    TileEntity(Tag tag) {
        this.tag = tag;
    }

    public String getId() {
        return tag.getTag("id").stringValue();
    }

    public int getX() {
        return tag.getTag("x").intValue();
    }

    public int getY() {
        return tag.getTag("y").intValue();
    }

    public int getZ() {
        return tag.getTag("z").intValue();
    }
}
