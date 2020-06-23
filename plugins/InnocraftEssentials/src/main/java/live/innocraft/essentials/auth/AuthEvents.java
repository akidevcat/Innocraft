package live.innocraft.essentials.auth;

import me.stefan911.securitymaster.lite.api.events.player.PlayerLoginEvent;
import me.stefan911.securitymaster.lite.api.events.player.PlayerRegisterEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

import java.util.logging.Level;

public class AuthEvents implements Listener {

    private final Auth auth;

    public AuthEvents(Auth auth) {
        this.auth = auth;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onConnect(PlayerJoinEvent event) {
        AuthPlayer authPlayer = auth.addAuthPlayer(event.getPlayer().getUniqueId());
        if (!authPlayer.isRegistered()) {
            event.getPlayer().kickPlayer(auth.getPlugin().getMessageColorFormat("registration-kick", "auth", event.getPlayer().getLocale(), authPlayer.getRegistrationCode()));
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDisconnect(PlayerQuitEvent event) {
        auth.removeAuthPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        AuthPlayer authPlayer = auth.getAuthPlayer(event.getPlayer().getUniqueId());
        if (authPlayer != null && !authPlayer.isLoggedIn()) {
            auth.getPlugin().sendChatMessage("auth-restricted-action", event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlaceEvent(BlockPlaceEvent event)
    {
        AuthPlayer authPlayer = auth.getAuthPlayer(event.getPlayer().getUniqueId());
        if (authPlayer != null && !authPlayer.isLoggedIn()) {
            auth.getPlugin().sendChatMessage("auth-restricted-action", event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreakEvent(BlockBreakEvent event)
    {
        AuthPlayer authPlayer = auth.getAuthPlayer(event.getPlayer().getUniqueId());
        if (authPlayer != null && !authPlayer.isLoggedIn()) {
            auth.getPlugin().sendChatMessage("auth-restricted-action", event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMoveEvent(PlayerMoveEvent event)
    {
        AuthPlayer authPlayer = auth.getAuthPlayer(event.getPlayer().getUniqueId());
        if (authPlayer != null && !authPlayer.isLoggedIn()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event)
    {
        AuthPlayer authPlayer = auth.getAuthPlayer(event.getPlayer().getUniqueId());
        if (authPlayer != null && !authPlayer.isLoggedIn()) {
            auth.getPlugin().sendChatMessage("auth-restricted-action", event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItemEvent(EntityPickupItemEvent event)
    {
        if (!(event.getEntity() instanceof Player))
            return;

        AuthPlayer authPlayer = auth.getAuthPlayer(event.getEntity().getUniqueId());
        if (authPlayer != null && !authPlayer.isLoggedIn()) {
            auth.getPlugin().sendChatMessage("auth-restricted-action", event.getEntity());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInventoryOpenEvent(InventoryOpenEvent event)
    {
        AuthPlayer authPlayer = auth.getAuthPlayer(event.getPlayer().getUniqueId());
        if (authPlayer != null && !authPlayer.isLoggedIn()) {
            auth.getPlugin().sendChatMessage("auth-restricted-action", event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInventoryClickEvent(InventoryClickEvent event)
    {
        AuthPlayer authPlayer = auth.getAuthPlayer(event.getWhoClicked().getUniqueId());
        if (authPlayer != null && !authPlayer.isLoggedIn()) {
            auth.getPlugin().sendChatMessage("auth-restricted-action", event.getWhoClicked());
            event.setCancelled(true);
        }
    }
}
