package live.innocraft.essentials.generator;

import live.innocraft.essentials.core.Essentials;
import live.innocraft.essentials.core.EssentialsModule;
import live.innocraft.essentials.generator.floors.green_hills.GreenHillsFloor;
import org.bukkit.generator.ChunkGenerator;

public class WorldGenerator extends EssentialsModule {

    public WorldGenerator(Essentials plugin) {
        super(plugin);
    }

    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        //TODO REFLECTIONS
        return new GreenHillsFloor(getPlugin()); //Testing
    }
}
