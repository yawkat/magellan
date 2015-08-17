package at.yawk.magellan.tools.impl;

import at.yawk.magellan.BlockCursor;
import at.yawk.magellan.Chunk;
import at.yawk.magellan.Section;
import at.yawk.magellan.TileEntity;
import at.yawk.magellan.tools.WorldProcessor;
import com.beust.jcommander.Parameter;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yawkat
 */
public class TileRemover extends WorldProcessor {
    @Parameter(names = "--id", required = true)
    int id;

    private final AtomicInteger removed = new AtomicInteger();

    @Override
    protected CompletionStage<Boolean> processChunk(Chunk chunk) {
        List<Section> sections = null;
        boolean edited = false;

        Iterator<TileEntity> iterator = chunk.getTileEntities().iterator();
        while (iterator.hasNext()) {
            TileEntity tile = iterator.next();

            if (sections == null) { sections = chunk.getSections(); }
            byte index = (byte) (tile.getY() / 16);
            for (Section section : sections) {
                if (section.getYIndex() == index) {
                    BlockCursor cursor = section.createCursor();
                    cursor.select(tile.getX(), tile.getY(), tile.getZ());
                    if (cursor.isId(id)) {
                        cursor.setNarrowId((byte) 0);
                        removed.incrementAndGet();
                        edited = true;
                        iterator.remove();
                    }
                    break;
                }
            }
        }
        return CompletableFuture.completedFuture(edited);
    }
}
