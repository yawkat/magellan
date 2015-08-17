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
}
