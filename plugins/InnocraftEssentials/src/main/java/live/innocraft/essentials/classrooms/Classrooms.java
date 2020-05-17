package live.innocraft.essentials.classrooms;

import live.innocraft.essentials.Essentials;
import live.innocraft.essentials.EssentialsHelper;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;

public class Classrooms {

    private final Essentials plugin;
    private final HashMap<String, String> regionsClassrooms;

    public Classrooms(Essentials plugin) {
        this.plugin = plugin;

        regionsClassrooms = new HashMap<String, String>();

        new ClassroomsCommands(plugin, this);
        new ClassroomsEvents(plugin, this);
    }

    public boolean CreateClassroom(String name) {
        Configuration cfg = plugin.GetConfiguration().GetCfgClassrooms();
        if (cfg.contains(name))
            return false;

        cfg.set(name + ".displayed-name", name);

        Save();

        return true;
    }

    public boolean DeleteClassroom(String name) {
        Configuration cfg = plugin.GetConfiguration().GetCfgClassrooms();
        if (cfg.contains(name))
            return false;

        cfg.set(name, null);

        Save();

        return true;
    }

    public boolean SetClassroomDisplayedName(String name, String displayedName) {
        Configuration cfg = plugin.GetConfiguration().GetCfgClassrooms();
        if (cfg.contains(name))
            return false;
        if (displayedName.equals(""))
            return false;

        cfg.set(name + ".displayed-name", displayedName);

        Save();

        return true;
    }

    public boolean SetClassroomRegion(String name, String region) {
        Configuration cfg = plugin.GetConfiguration().GetCfgClassrooms();
        if (cfg.contains(name))
            return false;

        if (region.equals(""))
            cfg.set(name + ".region", null);
        else
            cfg.set(name + ".region", region);

        Save();

        return true;
    }

    public boolean SetClassroomPosition(String name, Location pos) {
        Configuration cfg = plugin.GetConfiguration().GetCfgClassrooms();
        if (cfg.contains(name))
            return false;

        cfg.set(name + ".position", pos);

        Save();

        return true;
    }

    public boolean SetClassroomLink(String name, String link) {
        Configuration cfg = plugin.GetConfiguration().GetCfgClassrooms();
        if (cfg.contains(name))
            return false;

        if (link.equals(""))
            cfg.set(name + ".link", null);
        else
            cfg.set(name + ".link", link);

        Save();

        return true;
    }

    public String GetClassroomLink(String name) {
        Configuration cfg = plugin.GetConfiguration().GetCfgClassrooms();
        if (!cfg.contains(name) || !cfg.contains(name + ".link"))
            return "";

        return cfg.getString(name + ".link");
    }

    public String GetClassroomByPlayer(Player player) {
        String[] regions = EssentialsHelper.GetRegionsNames(player);
        if (regions.length == 0)
            return "";
        for (String r : regions) {
            if (regionsClassrooms.containsKey(r))
                return regionsClassrooms.get(r);
        }
        return "";
    }

    public void Save() {
        plugin.GetConfiguration().SaveClassrooms();
        UpdateRegions();
    }

    public void UpdateRegions() {
        regionsClassrooms.clear();

        Configuration cfg = plugin.GetConfiguration().GetCfgClassrooms();
        Set<String> keys = cfg.getKeys(false);

        for (String key : keys) {
            if (cfg.contains(key + ".region"))
                regionsClassrooms.put(cfg.getString(key + ".region"), key);
        }
    }
}
