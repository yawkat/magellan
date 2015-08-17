package at.yawk.magellan;

import at.yawk.magellan.nbt.Tag;
import lombok.Getter;

/**
 * @author yawkat
 */
public class Section {
    @Getter
    final Tag tag;

    Tag blocks;

    Section(Tag tag) {
        this.tag = tag;
        this.blocks = tag.getTag("Blocks");
    }

    public byte getYIndex() {
        return tag.getTag("Y").byteValue();
    }

    public BlockCursor createCursor() {
        return new CursorImpl();
    }

    private class CursorImpl implements BlockCursor {
        private int offset;

        @Override
        public void select(int x, int y, int z) {
            offset = ((y & 15) * 16 + (z & 15)) * 16 + (x & 15);
        }

        @Override
        public byte getNarrowId() {
            return blocks.getByte(offset);
        }

        @Override
        public void setNarrowId(byte id) {
            blocks.setByte(offset, id);
        }

        @Override
        public int getId() {
            int id = getNarrowId() & 0xff;
            Tag add = tag.getTag("Add");
            if (add != null) {
                id |= getNibble(add) << 8;
            }
            return id;
        }

        @Override
        public void setId(int id) {
            setNarrowId((byte) (id & 0xff));
            if ((id & 0xf00) != 0) {
                setNibble(tag.getTag("Add"), (byte) (id >>> 8));
            }
        }

        @Override
        public boolean isId(int id) {
            byte narrow = getNarrowId();
            if ((id & 0xff) != (narrow & 0xff)) {
                return false;
            }
            // getId is slow
            return getId() == id;
        }

        @Override
        public byte getData() {
            return getNibble(tag.getTag("Data"));
        }

        @Override
        public void setData(byte data) {
            setNibble(tag.getTag("Data"), data);
        }

        @Override
        public byte getBlockLight() {
            return getNibble(tag.getTag("BlockLight"));
        }

        @Override
        public void setBlockLight(byte blockLight) {
            setNibble(tag.getTag("BlockLight"), blockLight);
        }

        @Override
        public byte getSkyLight() {
            return getNibble(tag.getTag("SkyLight"));
        }

        @Override
        public void setSkyLight(byte skyLight) {
            setNibble(tag.getTag("SkyLight"), skyLight);
        }

        private void setNibble(Tag tag, byte nibble) {
            int i = offset >>> 1;
            byte b = tag.getByte(i);
            if (isUpperNibble()) {
                b = (byte) ((nibble << 4) | (b & 0x0f));
            } else {
                b = (byte) ((nibble & 0x0f) | (b & 0xf0));
            }
            tag.setByte(i, b);
        }

        private byte getNibble(Tag tag) {
            byte b = tag.getByte(offset >>> 1);
            return (byte) (isUpperNibble() ?
                    (b >>> 4) & 0x0f : (b) & 0x0f);
        }

        private boolean isUpperNibble() {
            return (offset & 1) == 0;
        }
    }
}
