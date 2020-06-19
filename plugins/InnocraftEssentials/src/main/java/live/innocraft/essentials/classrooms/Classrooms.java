package live.innocraft.essentials.classrooms;

import live.innocraft.essentials.common.Essentials;
import live.innocraft.essentials.helper.EssentialsHelper;
import live.innocraft.essentials.common.EssentialsModule;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;

public class Classrooms extends EssentialsModule {

    private final HashMap<String, String> regionsClassrooms;

    public Classrooms(Essentials plugin) {
        super(plugin);

        regionsClassrooms = new HashMap<String, String>();
    }

    @Override
    public void onLateInitialization() {
        new ClassroomsCommands(getPlugin(), this);
        new ClassroomsEvents(getPlugin(), this);
    }

    public boolean CreateClassroom(String name) {
        Configuration cfg = getPlugin().getConfiguration().GetCfgClassrooms();
        if (cfg.contains(name))
            return false;

        cfg.set(name + ".displayed-name", name);

        Save();

        return true;
    }

    public boolean DeleteClassroom(String name) {
        Configuration cfg = getPlugin().getConfiguration().GetCfgClassrooms();
        if (!cfg.contains(name))
            return false;

        cfg.set(name, null);

        Save();

        return true;
    }

    public boolean SetClassroomDisplayedName(String name, String displayedName) {
        Configuration cfg = getPlugin().getConfiguration().GetCfgClassrooms();
        if (!cfg.contains(name))
            return false;
        if (displayedName.equals(""))
            return false;

        cfg.set(name + ".displayed-name", displayedName);

        Save();

        return true;
    }

    public boolean SetClassroomRegion(String name, String region) {
        Configuration cfg = getPlugin().getConfiguration().GetCfgClassrooms();
        if (!cfg.contains(name))
            return false;

        if (region.equals(""))
            cfg.set(name + ".region", null);
        else
            cfg.set(name + ".region", region);

        Save();

        return true;
    }

    public boolean SetClassroomPosition(String name, Location pos) {
        Configuration cfg = getPlugin().getConfiguration().GetCfgClassrooms();
        if (!cfg.contains(name))
            return false;

        cfg.set(name + ".position", pos);

        Save();

        return true;
    }

    public boolean SetClassroomLink(String name, String link) {
        Configuration cfg = getPlugin().getConfiguration().GetCfgClassrooms();
        if (!cfg.contains(name))
            return false;

        if (link.equals(""))
            cfg.set(name + ".link", null);
        else
            cfg.set(name + ".link", link);

        Save();

        return true;
    }

    public boolean SetClassroomCode(String name, String code) {
        Configuration cfg = getPlugin().getConfiguration().GetCfgClassrooms();
        if (!cfg.contains(name))
            return false;

        if (code.equals(""))
            cfg.set(name + ".code", null);
        else
            cfg.set(name + ".code", code);

        Save();

        return true;
    }

    public String GetClassroomLink(String name) {
        Configuration cfg = getPlugin().getConfiguration().GetCfgClassrooms();
        if (!cfg.contains(name) || !cfg.contains(name + ".link"))
            return "";

        return cfg.getString(name + ".link");
    }

    public String GetClassroomCode(String name) {
        Configuration cfg = getPlugin().getConfiguration().GetCfgClassrooms();
        if (!cfg.contains(name) || !cfg.contains(name + ".code"))
            return "";

        return cfg.getString(name + ".code");
    }

    public String GetClassroomDisplayedName(String name) {
        Configuration cfg = getPlugin().getConfiguration().GetCfgClassrooms();
        if (!cfg.contains(name) || !cfg.contains(name + ".displayed-name"))
            return "";

        return cfg.getString(name + ".displayed-name");
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
        getPlugin().getConfiguration().SaveClassrooms();
        UpdateRegions();
    }

    @Override
    public void onReload() {
        //ToDo
    }

    public void UpdateRegions() {
        regionsClassrooms.clear();

        Configuration cfg = getPlugin().getConfiguration().GetCfgClassrooms();
        Set<String> keys = cfg.getKeys(false);

        for (String key : keys) {
            if (cfg.contains(key + ".region"))
                regionsClassrooms.put(cfg.getString(key + ".region"), key);
        }
    }
}
