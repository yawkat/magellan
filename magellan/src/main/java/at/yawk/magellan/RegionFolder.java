package at.yawk.magellan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.ToString;

/**
 * @author yawkat
 */
@ToString
public class RegionFolder {
    private static final Pattern REGION_FILE_PATTERN =
            Pattern.compile("r\\.(-?\\d+)\\.(-?\\d+)\\.mca");

    private final Path path;

    private RegionFolder(Path path) {
        this.path = path;
    }

    public static RegionFolder create(Path path) {
        return new RegionFolder(path);
    }

    public Path getRegionPath(RegionPosition position) {
        return path.resolve("r." + position.getX() + "." + position.getZ() + ".mca");
    }

    public List<RegionPosition> getRegionPositions() throws IOException {
        if (!Files.isDirectory(path)) { return Collections.emptyList(); }
        return Files.list(path)
                .map(p -> REGION_FILE_PATTERN.matcher(p.getFileName().toString()))
                .filter(Matcher::matches)
                .map(result -> new RegionPosition(Integer.parseInt(result.group(1)), Integer.parseInt(result.group(2))))
                .collect(Collectors.toList());
    }
}
