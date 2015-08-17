package at.yawk.magellan.tools;

import com.beust.jcommander.IStringConverter;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author yawkat
 */
public class PathConverter implements IStringConverter<Path> {
    @Override
    public Path convert(String s) {
        return Paths.get(s);
    }
}
