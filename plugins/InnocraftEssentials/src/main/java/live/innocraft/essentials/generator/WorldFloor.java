package live.innocraft.essentials.generator;

import live.innocraft.essentials.core.Essentials;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public abstract class WorldFloor extends ChunkGenerator {

    private final Essentials plugin;
    private final WorldGeneratorConfiguration cfg;

    private final int floorRadius;
    private final int floorRadius2;
    private final int outerFloorRadius;
    private final int outerFloorRadius2;

    public WorldFloor(Essentials plugin) {
        this.plugin = plugin;
        this.cfg = plugin.getConfiguration(WorldGeneratorConfiguration.class);

        floorRadius = getConfiguration().getFloorRadius();
        floorRadius2 = floorRadius * floorRadius;
        outerFloorRadius = floorRadius + 30;
        outerFloorRadius2 = outerFloorRadius * outerFloorRadius;
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {

        ChunkData chunkData = createChunkData(world);

        generateFloorChunkData(world, random, chunkX, chunkZ, biome, chunkData);

        generatePostFloorFeatures(world, random, chunkX, chunkZ, biome, chunkData);

        return chunkData;

    }

    protected abstract void generateFloorChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome, ChunkData chunkData);

    private void generatePostFloorFeatures(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome, ChunkData chunkData) {

        // Set level floor
        chunkData.setRegion(0, 0, 0, 16, 1, 16, Material.BEDROCK);

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int z = 0; z < 16; z++) {

                    // Global coordinates
                    int gx = x + chunkX * 16;
                    int gz = z + chunkZ * 16;

                    // Radius^2 for this block from (0, 0)
                    int r2 = (gx * gx + gz * gz);

                    if (r2 > outerFloorRadius2) {
                        chunkData.setBlock(x, y, z, Material.AIR);
                    } else if (r2 >= floorRadius2) {
                        if (r2 <= (floorRadius + 10) * (floorRadius + 10)) {
                            if (r2 <= (floorRadius + 1) * (floorRadius + 1) && (x + z) % 4 == 0 ||
                                r2 >= (floorRadius + 9) * (floorRadius + 9) && (x + z) % 4 == 0)
                                chunkData.setBlock(x, y, z, Material.BEDROCK);
                            else if (y <= 150f)
                                chunkData.setBlock(x, y, z, Material.BEDROCK);
                            else
                                chunkData.setBlock(x, y, z, Material.AIR);
                            if (y == 255)
                                chunkData.setBlock(x, y, z, Material.BEDROCK);
                        } else {
                            if (y <= (outerFloorRadius2 - r2) / (float)(outerFloorRadius2 - (floorRadius + 10) * (floorRadius + 10)) * 150f)
                                chunkData.setBlock(x, y, z, Material.BEDROCK);
                        }
                    }

                }
            }
        }

    }

    public Essentials getPlugin() {
        return plugin;
    }

    public WorldGeneratorConfiguration getConfiguration() {
        return cfg;
    }
}
