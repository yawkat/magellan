package at.yawk.magellan.tools;

import at.yawk.magellan.*;
import at.yawk.rjoin.zlib.ZlibException;
import com.beust.jcommander.Parameter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yawkat
 */
@Slf4j
public class WorldProcessor extends Application {
    @Parameter(
            description = "world folders",
            required = true
    )
    private List<Path> worldFolders;

    @Override
    public void run() throws Exception {
        for (Path worldFolder : worldFolders) {
            Path name = worldFolder.toAbsolutePath().getFileName();
            log.info("Processing world '{}'...", name);
            processWorld(WorldFolder.create(worldFolder)).thenRun(() -> {
                log.info("Processed world '{}'", name);
                onComplete();
            });
        }
    }

    protected void onComplete() {}

    protected CompletionStage<?> processWorld(WorldFolder worldFolder) throws Exception {
        CompletionStage<?> completion = CompletableFuture.completedFuture(null);
        for (int index : worldFolder.getRegionIndexes()) {
            RegionFolder folder = worldFolder.getRegionFolder(index);

            log.info("Processing region folder {}", folder);
            completion = completion.thenCombine(
                    processRegionFolder(folder),
                    (a, b) -> null
            ).thenRun(() -> log.info("Processed region folder {}", folder));
        }
        return completion;
    }

    protected CompletionStage<?> processRegionFolder(RegionFolder regionFolder) throws Exception {
        List<RegionPosition> regions = regionFolder.getRegionPositions();
        if (regions.isEmpty()) {
            log.warn("No regions found.");
            return CompletableFuture.completedFuture(null);
        }

        CompletionStage<?> completionStage = CompletableFuture.completedFuture(null);
        for (RegionPosition region : regions) {
            completionStage = completionStage.thenCombine(executeLogging(() -> {
                log.info("Processing region {}...", region);
                Path path = regionFolder.getRegionPath(region);
                RegionFile file = RegionFile.load(path);
                processRegion(file).thenAccept(save -> {
                    if (save) {
                        log.info("Processed region {} with changes, saving...", region);
                        synchronized (file) {
                            file.compact();
                            try {
                                file.save(path);
                            } catch (IOException e) {
                                log.error("Failed to save region {}", region, e);
                            }
                        }
                        log.info("Saved {}.", region);
                    } else {
                        log.info("Processed region {} without changes", region);
                    }
                });
                return null;
            }), (a, b) -> null);
        }
        return completionStage;
    }

    /**
     * @param file Region file. Synchronize write access properly!
     */
    protected CompletionStage<Boolean> processRegion(RegionFile file) throws Exception {
        // future contains how many chunks were changed
        CompletionStage<Integer> saveStage = CompletableFuture.completedFuture(0);
        for (ChunkPosition position : file.getChunkPositions()) {

            Chunk chunk = file.getChunk(position);
            CompletionStage<Boolean> chunkStage = processChunk(chunk);
            chunkStage.thenAccept(save -> {
                if (save) {
                    synchronized (file) {
                        try {
                            file.saveChunk(position.getRegionX(), position.getRegionZ(), chunk);
                        } catch (ZlibException e) {
                            log.error("Failed to save chunk", e);
                        }
                    }
                }
            });
            // increment save stage if changed
            saveStage = saveStage.thenCombine(chunkStage.thenApply(
                    save -> save ? 1 : 0), (a, b) -> a + b);
        }

        saveStage.thenAccept(modifiedCount -> {
            if (modifiedCount > 0) {
                log.debug("{} chunks modified in this region", modifiedCount);
            }
        });

        return saveStage.thenApply(modifiedCount -> modifiedCount > 0);
    }

    /**
     * @return A CompletionStage containing whether the chunk was modified and needs to be saved.
     */
    protected CompletionStage<Boolean> processChunk(Chunk chunk) {
        return CompletableFuture.completedFuture(false);
    }
}
