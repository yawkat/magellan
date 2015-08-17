package at.yawk.magellan.tools.impl;

import at.yawk.magellan.Chunk;
import at.yawk.magellan.tools.Bootstrap;
import at.yawk.magellan.tools.WorldProcessor;
import com.beust.jcommander.Parameter;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.jcip.annotations.GuardedBy;

/**
 * @author yawkat
 */
@Slf4j
public class TileCounter extends WorldProcessor {
    @Parameter(names = "--count")
    int count = 10;

    @GuardedBy("topChunks")
    private final Queue<ChunkEntry> topChunks = new PriorityQueue<>();

    public static void main(String[] args) {
        Bootstrap.start(TileCounter.class, args);
    }

    @Override
    protected void onComplete() {
        synchronized (topChunks) {
            while (!topChunks.isEmpty()) {
                ChunkEntry chunk = topChunks.poll();
                log.info("Chunk x={} z={} has {} tiles", chunk.getX(), chunk.getZ(), chunk.getCount());
            }
        }
    }

    @Override
    protected CompletionStage<Boolean> processChunk(Chunk chunk) {
        ChunkEntry entry = new ChunkEntry(
                chunk.getChunkX(), chunk.getChunkZ(), chunk.getTileEntities().size());
        synchronized (topChunks) {
            topChunks.offer(entry);
            while (topChunks.size() > count) {
                topChunks.remove();
            }
        }
        return CompletableFuture.completedFuture(false);
    }

    @Value
    private static class ChunkEntry implements Comparable<ChunkEntry> {
        private final int x;
        private final int z;
        private final int count;

        @Override
        public int compareTo(ChunkEntry o) {
            return this.count - o.count;
        }
    }
}
