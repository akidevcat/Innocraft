package live.innocraft.smbridge;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigFile {

    private final String fileName;
    private Configuration cfg;
    private final Plugin plugin;
    private boolean isLoaded;

    public ConfigFile(Plugin plugin, String name) {
        this.plugin = plugin;
        this.fileName = name;
        Load();
    }

    public boolean Save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, new File(plugin.getDataFolder(), fileName));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean Load() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            try (InputStream in = plugin.getResourceAsStream(fileName)) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                isLoaded = false;
                return false;
            }
        }

        try {
            cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
            isLoaded = false;
            return false;
        }

        isLoaded = true;
        return true;
    }

    public boolean GetLoadedState() {
        return isLoaded;
    }

    public Configuration GetConfiguration() {
        return cfg;
    }
}
