package at.yawk.magellan;

import at.yawk.magellan.nbt.RootTag;
import at.yawk.magellan.nbt.Tag;
import at.yawk.magellan.nbt.TagReader;
import at.yawk.magellan.nbt.TagWriter;
import at.yawk.magellan.nbt.lexer.Lexer;
import at.yawk.rjoin.zlib.ZDeflater;
import at.yawk.rjoin.zlib.ZInflater;
import at.yawk.rjoin.zlib.Zlib;
import at.yawk.rjoin.zlib.ZlibException;
import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author yawkat
 */
@EqualsAndHashCode
public class Chunk {
    @Getter
    private final Tag root;

    private Chunk(Tag root) {
        this.root = root;
    }

    ///// SERIALIZATION

    public static Chunk fromTag(Tag root) {
        return new Chunk(root);
    }

    public static Chunk fromBuffer(ByteBuffer buffer) {
        return fromBuffer(buffer, false);
    }

    public static Chunk fromBuffer(ByteBuffer buffer, boolean maySliceInput) {
        Lexer lexer = Lexer.create();
        lexer.setInput(buffer, maySliceInput);
        RootTag rootTag = TagReader.create(lexer).parse();
        return fromTag(rootTag.getTag());
    }

    public static Chunk fromCompressedBuffer(ByteBuffer buffer) throws ZlibException {
        byte compressionType = buffer.get();
        switch (compressionType) {
        case 2: // zlib
            try (ZInflater inflater = Zlib.getProvider().createInflater()) {
                return fromBuffer(BufferUtil.deflateInflate(inflater, buffer, 0), true);
            }
        default:
            throw new UnsupportedOperationException("Unsupported compression type " + compressionType);
        }
    }

    public ByteBuffer toBuffer() {
        return TagWriter.toBuffer(new RootTag("", this.root));
    }

    public ByteBuffer toCompressedBuffer() throws ZlibException {
        ByteBuffer input = toBuffer();
        try (ZDeflater deflater = Zlib.getProvider().createDeflater()) {
            // 1 byte padding for compression type
            ByteBuffer result = BufferUtil.deflateInflate(deflater, input, 1);
            result.put(0, (byte) 2); // zlib
            return result;
        }
    }

    ///// DATA

    public List<Section> getSections() {
        return getMappedCompoundList("Sections", Section::new);
    }

    public List<Entity> getEntities() {
        return getMappedCompoundList("Entities", Entity::new);
    }

    public List<TileEntity> getTileEntities() {
        return getMappedCompoundList("TileEntities", TileEntity::new);
    }

    private <T> List<T> getMappedCompoundList(String tagName, Function<Tag, T> factory) {
        Tag tag = getLevelTag().getTag(tagName);
        return new MappedTagList<>(tag, factory);
    }

    public int getChunkX() {
        return getLevelTag().getTag("xPos").intValue();
    }

    public int getChunkZ() {
        return getLevelTag().getTag("zPos").intValue();
    }

    private Tag getLevelTag() {
        return root.getTag("Level");
    }

    private static class MappedTagList<T> extends AbstractList<T> implements RandomAccess {
        private final Tag tag;
        private final Function<Tag, T> factory;

        public MappedTagList(Tag tag, Function<Tag, T> factory) {
            this.tag = tag;
            this.factory = factory;
        }

        @Override
        public T get(int index) {
            return factory.apply(tag.getTag(index));
        }

        @Override
        public T remove(int index) {
            T t = get(index);
            tag.removeTag(index);
            return t;
        }

        @Override
        public int size() {
            return tag.size();
        }
    }
}
