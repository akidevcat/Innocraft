package live.innocraft.essentials;

import live.innocraft.essentials.authkeys.AuthKeysCommands;
import live.innocraft.essentials.classrooms.Classrooms;
import live.innocraft.essentials.timetable.Timetable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Essentials extends JavaPlugin {

    private EssentialsConfiguration essentialsCfg;

    private Classrooms mClassrooms;
    private Timetable mTimetable;

    // Reloads configuration files
    public void ReloadConfigurations() {
        essentialsCfg.ReloadAll();
    }

    public EssentialsConfiguration GetConfiguration() {
        return essentialsCfg;
    }

    public Classrooms GetClassrooms() { return mClassrooms; }

    @Override
    public void onEnable() {
        //Load configuration files
        essentialsCfg = new EssentialsConfiguration(this);

        //Enable Core module
        new EssentialsCommands(this);
        new EssentialsPlaceholderExpansion(this);

        //Enable AuthKeys module
        new AuthKeysCommands(this);

        //Enable Classrooms module
        mClassrooms = new Classrooms(this);

        //Enable Timetable module
        mTimetable = new Timetable(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void ReloadAll() {
        ReloadConfigurations();
        mClassrooms.Reload();
        mTimetable.Reload();
    }
}
