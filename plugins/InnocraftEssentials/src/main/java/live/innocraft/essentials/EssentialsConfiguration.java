package live.innocraft.essentials;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.logging.Level;

public class EssentialsConfiguration {

    private final Plugin plugin;

    private FileConfiguration cfgCommon;
    private FileConfiguration cfgAuthKeys;
    private FileConfiguration cfgClassrooms;
    private FileConfiguration cfgTimetable;

    public EssentialsConfiguration(Plugin plugin) {
        this.plugin = plugin;

        ReloadAll();
    }

    public void ReloadAll() {
        cfgCommon = LoadConfigFile("config.yml");
        cfgAuthKeys = LoadConfigFile("authkeys.yml");
        cfgClassrooms = LoadConfigFile("classrooms.yml");
        cfgTimetable = LoadConfigFile("timetable.yml");
    }

    public void ReloadAuthKeys() {
        cfgAuthKeys = LoadConfigFile("authkeys.yml");
    }

    public void SaveAuthKeys() {
        try {
            cfgAuthKeys.save(new File(plugin.getDataFolder(), "authkeys.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SaveClassrooms() {
        try {
            cfgAuthKeys.save(new File(plugin.getDataFolder(), "classrooms.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SaveTimetable() {
        try {
            cfgAuthKeys.save(new File(plugin.getDataFolder(), "timetable.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sends a command to the specified player with color codes basing on the config.yml
    public void SendMessage(String msg, CommandSender s) {
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', cfgCommon.getString("messages." + msg)));
    }

    public FileConfiguration GetCfgCommon() {
        return cfgCommon;
    }

    public FileConfiguration GetCfgAuthKeys() {
        return cfgAuthKeys;
    }

    public FileConfiguration GetCfgClassrooms() {
        return cfgClassrooms;
    }

    public FileConfiguration GetCfgTimetable() {
        return cfgTimetable;
    }

    // Loads a configuration file. In case it doesn't exist, uses a default template.
    private FileConfiguration LoadConfigFile(String name) {
        FileConfiguration result;
        result = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), name));
        if (result.saveToString().length() == 0) {
            result = YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(plugin.getResource(name)), StandardCharsets.UTF_8));
            try {
                result.save(new File(plugin.getDataFolder(), name));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
