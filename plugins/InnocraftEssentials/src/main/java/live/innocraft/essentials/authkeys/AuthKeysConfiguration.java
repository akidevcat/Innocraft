package live.innocraft.essentials.authkeys;

import live.innocraft.essentials.core.Essentials;
import live.innocraft.essentials.core.EssentialsConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public class AuthKeysConfiguration extends EssentialsConfiguration {

    private final HashMap<String, AuthKeyGroup> groups;

    public AuthKeysConfiguration(Essentials plugin) {
        super(plugin, "authkeys.yml", true);

        groups = new HashMap<>();
    }

    public AuthKeyGroup getGroup(String name) {
        return groups.get(name);
    }

    @Override
    public void onReload() {
        ConfigurationSection section = getCfgFile().getConfigurationSection("groups");
        for (String key : section.getKeys(false)) {
            String[] roles = section.getStringList("groups." + key + ".roles").toArray(new String[0]);
            String[] groups = section.getStringList("groups." + key + ".groups").toArray(new String[0]);
            this.groups.put(key, new AuthKeyGroup(roles, groups));
        }
    }

}
