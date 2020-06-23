package live.innocraft.essentials.core;

import live.innocraft.essentials.core.Essentials;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public abstract class EssentialsConfiguration {

    private final Essentials plugin;

    private final String name;
    private final boolean hasTemplate;
    private FileConfiguration cfgFile;

    public EssentialsConfiguration(Essentials plugin, String name, boolean hasTemplate) {
        this.plugin = plugin;
        this.name = name;
        this.hasTemplate = hasTemplate;
    }

    public FileConfiguration getCfgFile() {
        return cfgFile;
    }

    public void loadFile() {
        //Try to load file
        cfgFile = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), name));

        //In case there's no such file or it's empty
        if (cfgFile.saveToString().length() == 0 && hasTemplate) {
            cfgFile = YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(plugin.getResource(name)), StandardCharsets.UTF_8));
            try {
                cfgFile.save(new File(plugin.getDataFolder(), name));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.onReload();
    }

    public void saveFile() {
        try {
            cfgFile.save(new File(plugin.getDataFolder(), name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Overridable Methods
    public void onLateInitialization() {}
    public void onReload() {}
}
