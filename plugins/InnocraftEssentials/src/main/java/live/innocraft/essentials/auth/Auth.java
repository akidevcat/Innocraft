package live.innocraft.essentials.auth;

import live.innocraft.essentials.Essentials;
import live.innocraft.essentials.EssentialsModule;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class Auth extends EssentialsModule {

    private HashMap<UUID, String> discordCache;

    public Auth(Essentials plugin) {
        super(plugin);

        discordCache = new HashMap<>();

        if (getPlugin().getServerType().equals("auth"))
            getServer().getPluginManager().registerEvents(new AuthEvents(this), plugin);
    }

    @Override
    public void Reload() {

    }

    @Override
    public void LateInitialization() {

    }

    public void RegisterPlayer(UUID uniqueID, String DiscordID) {

    }

    public void RegisterPlayer(Player player, String DiscordID) {
        RegisterPlayer(player.getUniqueId(), DiscordID);
    }

    public void CachePlayer(Player player, String DiscordID) {
        CachePlayer(player.getUniqueId(), DiscordID);
    }

    public void CachePlayer(UUID uniqueID, String DiscordID) {
        discordCache.put(uniqueID, DiscordID);
    }

    public void DeleteCachePlayer(Player player) {
        DeleteCachePlayer(player.getUniqueId());
    }

    public void DeleteCachePlayer(UUID uniqueID) {
        discordCache.remove(uniqueID);
    }

    public String getDiscordID(Player player) {
        return getDiscordID(player.getUniqueId());
    }

    public String getDiscordID(UUID uniqueID) {
        return discordCache.get(uniqueID);
    }
}
