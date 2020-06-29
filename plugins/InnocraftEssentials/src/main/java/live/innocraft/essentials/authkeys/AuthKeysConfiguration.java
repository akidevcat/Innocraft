package live.innocraft.essentials.authkeys;

import live.innocraft.essentials.core.Essentials;
import live.innocraft.essentials.core.EssentialsConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class AuthKeysConfiguration extends EssentialsConfiguration {

    private final HashMap<String, AuthKeyPermGroup> permGroups;
    private final ArrayList<String> discordRoles;

    public AuthKeysConfiguration(Essentials plugin) {
        super(plugin, "authkeys.yml", true);

        permGroups = new HashMap<>();
        discordRoles = new ArrayList<>();
    }

    public AuthKeyPermGroup getGroup(String name) {
        return permGroups.get(name);
    }

    @Override
    public void onReload() {
        discordRoles.clear();
        ConfigurationSection section = getCfgFile().getConfigurationSection("perm-groups");
        for (String key : section.getKeys(false)) {
            String[] roles = section.getStringList(key + ".roles").toArray(new String[0]);
            String perm = section.getString(key + ".perm");
            this.permGroups.put(key, new AuthKeyPermGroup(roles, perm));
            discordRoles.addAll(Arrays.asList(roles));
        }
    }

    public AuthKeyPermGroup getPermGroup(String name) {
        return permGroups.get(name);
    }

    public ArrayList<String> getDiscordRoles () {
        return new ArrayList<>(discordRoles);
    }

}
