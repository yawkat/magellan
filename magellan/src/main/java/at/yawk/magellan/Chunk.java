package at.yawk.magellan;

import at.yawk.magellan.nbt.RootTag;
import at.yawk.magellan.nbt.Tag;
import at.yawk.magellan.nbt.TagReader;
import at.yawk.magellan.nbt.lexer.Lexer;
import at.yawk.rjoin.zlib.ZInflater;
import at.yawk.rjoin.zlib.Zlib;
import at.yawk.rjoin.zlib.ZlibException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * @author yawkat
 */
public class Chunk {
    @Getter
    private final Tag root;

    private Chunk(Tag root) {
        this.root = root;
    }

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
            if (Zlib.supportsNative() && !buffer.isDirect()) {
                ByteBuffer direct = ByteBuffer.allocateDirect(buffer.remaining());
                direct.put(buffer);
                buffer = direct;
                buffer.flip();
            }

            try (ZInflater inflater = Zlib.getProvider().createInflater()) {
                inflater.setInput(buffer);

                int totalSize = 0;
                List<ByteBuffer> outBuffers = new ArrayList<>();
                while (!inflater.finished()) {
                    ByteBuffer partBuffer = ByteBuffer.allocateDirect(4096);
                    inflater.inflate(partBuffer);
                    outBuffers.add(partBuffer);
                    int inflated = partBuffer.position();
                    if (inflated == 0) {
                        throw new ZlibException("No data inflated");
                    }
                    totalSize = Math.addExact(totalSize, inflated);
                }

                ByteBuffer sumBuffer = ByteBuffer.allocateDirect(totalSize);
                for (ByteBuffer component : outBuffers) {
                    component.flip();
                    sumBuffer.put(component);
                }
                sumBuffer.flip();
                return fromBuffer(sumBuffer, true);
            }
        default:
            throw new UnsupportedOperationException("Unsupported compression type " + compressionType);
        }
    }

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
        return root.getTag("Level")
                .getTag(tagName).stream()
                .map(factory)
                .collect(Collectors.toList());
    }
}
