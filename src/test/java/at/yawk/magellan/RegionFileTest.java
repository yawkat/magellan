package at.yawk.magellan;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author yawkat
 */
public class RegionFileTest {
    @Test
    public void testLoad() throws IOException {
        ByteBuffer buffer = Util.loadResource(RegionFileTest.class.getResource("region.mca"));
        RegionFile regionFile = RegionFile.fromBuffer(buffer);

        ChunkPosition position = new ChunkPosition((byte) 1, (byte) 15, 2, (byte) 2);
        Assert.assertEquals(regionFile.getChunkPositions(), Collections.singletonList(position));

        Chunk chunk = regionFile.getChunk(position);
    }

}