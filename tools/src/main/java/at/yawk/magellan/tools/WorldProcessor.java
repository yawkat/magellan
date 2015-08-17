package at.yawk.magellan.tools;

import at.yawk.magellan.*;
import com.beust.jcommander.Parameter;
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
            processWorld(WorldFolder.create(worldFolder))
                    .thenRun(() -> log.info("Processed world '{}'", name));
        }
    }

    protected CompletionStage<?> processWorld(WorldFolder worldFolder) throws Exception {
        List<RegionPosition> regions = worldFolder.getRegionPositions();
        if (regions.isEmpty()) {
            log.warn("No regions found.");
            return CompletableFuture.completedFuture(null);
        }

        CompletionStage<?> completionStage = CompletableFuture.completedFuture(null);
        for (RegionPosition region : regions) {
            completionStage = completionStage.thenCombine(executeLogging(() -> {
                log.info("Processing region {}...", region);
                RegionFile file = RegionFile.load(worldFolder.getRegionPath(region));
                processRegion(file).thenAccept(save -> {
                    if (save) {
                        log.info("Processed region {} with changes, saving...", region);
                        // todo save
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

    protected CompletionStage<Boolean> processRegion(RegionFile file) throws Exception {
        CompletionStage<Boolean> saveStage = CompletableFuture.completedFuture(false);
        for (ChunkPosition position : file.getChunkPositions()) {
            CompletionStage<Boolean> chunkStage = processChunk(file.getChunk(position));
            chunkStage.thenAccept(save -> {
                // todo: save to region file
            });
            saveStage = saveStage.thenCombine(chunkStage, (a, b) -> a | b);
        }
        return saveStage;
    }

    protected CompletionStage<Boolean> processChunk(Chunk chunk) {
        return CompletableFuture.completedFuture(false);
    }
}
