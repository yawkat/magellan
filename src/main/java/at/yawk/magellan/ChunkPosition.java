package at.yawk.magellan;

import lombok.Value;

/**
 * @author yawkat
 */
@Value
public class ChunkPosition {
    /**
     * X-coordinate of this chunk in this region file: {@code [0;32[}
     */
    private final byte regionX;
    /**
     * Z-coordinate of this chunk in this region file: {@code [0;32[}
     */
    private final byte regionZ;
    /**
     * Sector offset of this chunk.
     */
    private final int offset;
    /**
     * Sector count of this chunk.
     */
    private final byte sectorCount;
}
