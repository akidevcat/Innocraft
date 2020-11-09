package live.innocraft.essentials.generator;

import live.innocraft.essentials.core.Essentials;
import live.innocraft.essentials.core.EssentialsConfiguration;

public class WorldGeneratorConfiguration extends EssentialsConfiguration {

    private int floorRadius = 1000;

    public WorldGeneratorConfiguration(Essentials plugin) {
        super(plugin, "generator.yml", true);
    }

    @Override
    public void onReload() {
        floorRadius = getCfgFile().getInt("floor-radius");
    }

    public int getFloorRadius() {
        return floorRadius;
    }

}
