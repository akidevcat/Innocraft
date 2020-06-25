package live.innocraft.essentialsbungee.auth;

import live.innocraft.essentialsbungee.core.EssentialsBungee;
import live.innocraft.essentialsbungee.core.EssentialsModule;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.logging.Level;

public class Auth extends EssentialsModule {

    private final HashSet<ProxiedPlayer> restrictedPlayers;

    public Auth(EssentialsBungee plugin) {
        super(plugin);

        restrictedPlayers = new HashSet<>();

        getPlugin().getProxy().getPluginManager().registerListener(getPlugin(), new AuthEvents(this));
    }

    public void addRestrictedPlayer(ProxiedPlayer player) {
        restrictedPlayers.add(player);
    }

    public void removeRestrictedPlayer(ProxiedPlayer player) {
        restrictedPlayers.remove(player);
    }

    public boolean containsRestrictedPlayer(ProxiedPlayer player) {
        return restrictedPlayers.contains(player);
    }

    public void removeBlockedPlayerString(String uuid) {
        removeRestrictedPlayer(getPlugin().getProxy().getPlayer(UUID.fromString(uuid)));
    }

    @Override
    public void onReload() {

    }

    @Override
    public void onLateInitialization() {

    }
}
