package at.yawk.magellan;

/**
 * @author yawkat
 */
public interface BlockCursor {
    void select(int x, int y, int z);

    byte getNarrowId();

    void setNarrowId(byte id);

    void setId(int id);

    int getId();

    /**
     * Equivalent to {@code getId() == id}, but may be faster
     */
    boolean isId(int id);

    byte getData();

    void setData(byte data);

    byte getBlockLight();

    void setBlockLight(byte blockLight);

    byte getSkyLight();

    void setSkyLight(byte skyLight);
}
