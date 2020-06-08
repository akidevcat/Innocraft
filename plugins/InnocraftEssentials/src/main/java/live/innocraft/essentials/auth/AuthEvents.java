package live.innocraft.essentials.auth;

import me.stefan911.securitymaster.lite.api.events.player.PlayerLoginEvent;
import me.stefan911.securitymaster.lite.api.events.player.PlayerRegisterEvent;
import me.stefan911.securitymaster.lite.utils.account.AccountManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Level;

public class AuthEvents implements Listener {

    private final Auth auth;

    public AuthEvents(Auth auth) {
        this.auth = auth;
    }

    @EventHandler
    public void onRegister(PlayerRegisterEvent event) {
        auth.getPlugin().getLogger().log(Level.SEVERE, "ON REGISTER");
        auth.CachePlayer(event.getPlayer(), event.getAccountManager().getDiscordID());
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        auth.getPlugin().getLogger().log(Level.SEVERE, "ON LOGIN");
        auth.CachePlayer(event.getPlayer(), event.getAccountManager().getDiscordID());
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        auth.getPlugin().getLogger().log(Level.SEVERE, "EXIT");
        auth.DeleteCachePlayer(event.getPlayer());
    }
}
