package live.innocraft.essentialsbungee.auth;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class AuthEvents implements Listener {

    private final Auth auth;

    public AuthEvents(Auth auth) {

        this.auth = auth;

    }

    @EventHandler
    public void onConnect(ServerConnectedEvent event) {
        auth.addRestrictedPlayer(event.getPlayer());
    }

    @EventHandler
    public void onDisconnect(ServerDisconnectEvent event) {
        auth.removeRestrictedPlayer(event.getPlayer());
    }

    @EventHandler (priority=EventPriority.HIGHEST)
    public void onMessage(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer))
            return;
        if (event.isCommand() && auth.containsRestrictedPlayer((ProxiedPlayer)event.getSender()))
            event.setCancelled(true);
    }

}
