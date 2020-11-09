package live.innocraft.essentials.generator.floors.green_hills;

import live.innocraft.essentials.core.Essentials;
import live.innocraft.essentials.generator.WorldFloor;
import live.innocraft.essentials.generator.WorldGenerator;
import live.innocraft.essentials.generator.WorldGeneratorCommon;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.noise.SimplexNoiseGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.Random;

public class GreenHillsFloor extends WorldFloor {

    private final int[][] heightmapCache;
    private SimplexNoiseGenerator simplex;

    public GreenHillsFloor(Essentials plugin) {
        super(plugin);

        heightmapCache = new int[16][16];
    }

    private void initializeVariables(long seed) {
        if (simplex == null) {
            simplex = new SimplexNoiseGenerator(new Random(seed));
        }
    }

    @Override
    protected void generateFloorChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome, ChunkData chunkData) {

        initializeVariables(world.getSeed());

        applyHeightmapGeneration(chunkData, chunkX, chunkZ, random);
    }

    private void applyHeightmapGeneration(ChunkData chunkData, int chunkX, int chunkZ, Random random) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int gx = x + chunkX * 16;
                int gz = z + chunkZ * 16;

                int h = (int)(WorldGeneratorCommon.fBmSimplex(gx * 0.0003D, gz * 0.0003D, simplex, 4, 0.4, 3.9) * 34 + 80);
                double islandh = Math.max(WorldGeneratorCommon.fBmSimplex(gx * 0.005D, gz * 0.005D, simplex, 3, 0.85, 1.5) - 0.7, 0) / 0.5;
                //islandh *= islandh;
                islandh *= 12;
                int island = h - (int)(islandh);
                //int h = (int)(simplex.noise(gx, gz, 0, 0) * 15 + 50);
                heightmapCache[x][z] = h;

                chunkData.setRegion(x, 1, z, x + 1, Math.min(h - 10, island), z + 1, Material.STONE);
                chunkData.setRegion(x, h-10, z, x + 1, Math.min(h, island), z + 1, Material.DIRT);
                if (h == island)
                    chunkData.setBlock(x, h, z, Material.GRASS_BLOCK);
                else {
                    chunkData.setBlock(x, h + 30, z, Material.GRASS_BLOCK);
                    chunkData.setRegion(x, island + 30, z, x + 1, h + 30, z + 1, Material.STONE);
                    if (random.nextFloat() < 0.05f)
                        chunkData.setRegion(x, island + 30 - (int)(random.nextFloat() * 15), z, x + 1, island + 30, z + 1, Material.VINE);
                        //chunkData.setBlock(x, island + 29, z, Material.VINE);
                }
            }
        }
    }


}
