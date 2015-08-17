package at.yawk.magellan;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

/**
 * @author yawkat
 */
public class Util {
    public static ByteBuffer loadResource(URL resource) throws IOException {
        try (InputStream inputStream = resource.openStream()) {
            return ByteBuffer.wrap(ByteStreams.toByteArray(inputStream));
        }
    }
}
