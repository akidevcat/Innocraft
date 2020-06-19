package live.innocraft.essentials.common;

import live.innocraft.essentials.helper.EssentialsHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;

public class EssentialsConfigurationDeprecated {

    private final Plugin plugin;

    private FileConfiguration cfgCommon;
    private FileConfiguration cfgAuthKeys;
    private FileConfiguration cfgClassrooms;
    private FileConfiguration cfgTimetable;
    private FileConfiguration logsAuthKeys;
    public EssentialsConfigurationDeprecated(Plugin plugin) {
        this.plugin = plugin;

        ReloadAll();
    }

    public void ReloadAll() {
        cfgCommon = LoadConfigFile("config.yml");
        cfgAuthKeys = LoadConfigFile("authkeys.yml");
        cfgClassrooms = LoadConfigFile("classrooms.yml");
        cfgTimetable = LoadConfigFile("timetable.yml");
        logsAuthKeys = LoadConfigFile("authkeys-logs.yml");
    }

    public void SyncTimetableCfg() {
        String url = cfgCommon.getString("github.urls.timetable");
        try {
            String content = EssentialsHelper.ReadURLContent(url);
            for (String key : cfgTimetable.getKeys(false))
                cfgTimetable.set(key, null);
            cfgTimetable.loadFromString(content);
            SaveTimetable();
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    public void ReloadAuthKeys() {
        cfgAuthKeys = LoadConfigFile("authkeys.yml");
    }

    public void ReloadTimetable() {
        cfgTimetable = LoadConfigFile("timetable.yml");
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
            cfgClassrooms.save(new File(plugin.getDataFolder(), "classrooms.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SaveTimetable() {
        try {
            cfgTimetable.save(new File(plugin.getDataFolder(), "timetable.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SaveAuthkeysLogs() {
        try {
            logsAuthKeys.save(new File(plugin.getDataFolder(), "authkeys-logs.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sends a command to the specified player with color codes basing on the config.yml
    public void SendMessage(String msg, CommandSender s) {
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', cfgCommon.getString("messages." + msg)));
    }

    public void SendMessageFormat(String msg, CommandSender s, String... args) {
        MessageFormat format = new MessageFormat(cfgCommon.getString("messages." + msg));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', format.format(args)));
    }

    public String GetMessage(String msg) {
        return ChatColor.translateAlternateColorCodes('&', cfgCommon.getString("messages." + msg));
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

    public FileConfiguration GetAuthKeysLogs() { return logsAuthKeys; }

    public void LogAuthKeys(String subcategory, String data, String time) {
        logsAuthKeys.set(time + "." + subcategory, data);
    }

    public void LogAuthKeys(String subcategory, String data, String time, boolean save) {
        logsAuthKeys.set(time + "." + subcategory, data);
        if (save)
            SaveAuthkeysLogs();
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
