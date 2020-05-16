package live.innocraft.essentials;

import live.innocraft.essentials.authkeys.AuthKeysCommands;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Essentials extends JavaPlugin {

    private EssentialsConfiguration essentialsCfg;

    // Reloads configuration files
    public void ReloadConfigurations() {
        essentialsCfg.ReloadAll();
    }

    public EssentialsConfiguration GetConfiguration() {
        return essentialsCfg;
    }

    @Override
    public void onEnable() {
        //Load configuration files
        essentialsCfg = new EssentialsConfiguration(this);

        //Enable Core module
        new EssentialsCommands(this);
        new EssentialsPlaceholderExpansion(this);

        //Enable AuthKeys module
        new AuthKeysCommands(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
