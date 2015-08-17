package at.yawk.magellan;

import at.yawk.magellan.nbt.Tag;
import lombok.Getter;

/**
 * @author yawkat
 */
public class Entity {
    @Getter
    private final Tag tag;

    Entity(Tag tag) {
        this.tag = tag;
    }
}
