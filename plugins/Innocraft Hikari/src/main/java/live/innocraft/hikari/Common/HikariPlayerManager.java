package live.innocraft.hikari.Common;

import live.innocraft.hikari.PluginCore.HikariPlugin;
import live.innocraft.hikari.PluginCore.HikariPluginModule;

import java.util.HashMap;
import java.util.UUID;

public class HikariPlayerManager extends HikariPluginModule {

    private final HashMap<UUID, HikariPlayer> hikariPlayers;

    public HikariPlayerManager(HikariPlugin plugin) {
        super(plugin);

        hikariPlayers = new HashMap<>();
    }

    public void createHikariPlayer(UUID uuid) {
        hikariPlayers.put(uuid, new HikariPlayer(uuid));
    }

    public void removeHikariPlayer(UUID uuid) {
        hikariPlayers.remove(uuid);
    }

    public HikariPlayer getHikariPlayer(UUID uuid) {
        return hikariPlayers.get(uuid);
    }

}
