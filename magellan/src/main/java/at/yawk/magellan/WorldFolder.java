package at.yawk.magellan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author yawkat
 */
public class WorldFolder {
    private static final Pattern REGION_FOLDER_PATTERN =
            Pattern.compile("(?:region|DIM(-?\\d+))");

    private final Path path;

    private WorldFolder(Path path) {
        this.path = path;
    }

    public static WorldFolder create(Path path) {
        return new WorldFolder(path);
    }

    public RegionFolder getRegionFolder(int dimensionIndex) {
        if (dimensionIndex == 0) {
            return RegionFolder.create(path.resolve("region"));
        } else {
            return RegionFolder.create(path.resolve("DIM" + dimensionIndex));
        }
    }

    public List<Integer> getRegionIndexes() throws IOException {
        if (!Files.isDirectory(path)) { return Collections.emptyList(); }
        return Files.list(path)
                .map(p -> REGION_FOLDER_PATTERN.matcher(p.getFileName().toString()))
                .filter(Matcher::matches)
                .map(result -> result.group(1))
                .map(index -> index == null ? 0 : Integer.parseInt(index))
                .collect(Collectors.toList());
    }
}
