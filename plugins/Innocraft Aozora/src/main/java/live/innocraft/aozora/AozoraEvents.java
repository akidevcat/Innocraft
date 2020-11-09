package live.innocraft.aozora;

import live.innocraft.aozora.Structures.AuthPlayer;
import live.innocraft.hikari.Helper.HikariHelper;
import live.innocraft.hikari.HikariCore;
import org.bukkit.Bukkit;
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

public class AozoraEvents implements Listener {

    private final HikariCore hikariCore;
    private final AozoraManager manager;

    public AozoraEvents (AozoraManager manager) {
        this.manager = manager;
        this.hikariCore = HikariCore.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onConnect(PlayerJoinEvent event) {
        AuthPlayer authPlayer = manager.addAuthPlayer(event.getPlayer().getUniqueId());
        if (!HikariHelper.isNicknameValid(event.getPlayer().getName())) {
            event.getPlayer().kickPlayer(hikariCore.getMessageColor("invalid-nickname-kick", "auth", event.getPlayer().getLocale()));
            return;
        }
        if (!authPlayer.isRegistered()) {
            event.getPlayer().kickPlayer(hikariCore.getMessageColorFormat("registration-kick", "auth", event.getPlayer().getLocale(), authPlayer.getRegistrationCode()));
            return;
        }
        if (hikariCore.getServerType().equals("auth"))
            hikariCore.sendChatMessageFormatLang("login-request", event.getPlayer(), event.getPlayer().getLocale(), event.getPlayer().getDisplayName());
    }

    protected void onLogin(AuthPlayer authPlayer) {

        Player player = Bukkit.getPlayer(authPlayer.getUniqueID());

        //Register to Minecord
//        if (hikariCore.hasDependency("Minecord")) {
//            try {
//                MAPI minecord = hikariCore.getDependency(MAPI.class);
//                Discord discord = manager.getModule(Discord.class);
//                Member member = discord.getGuild().getMemberById(authPlayer.getDiscordID());
//                minecord.link(player, member);
//            } catch (Exception ex) {
//                //ex.printStackTrace();
//            }
//        }

        // Send welcome message
//        hikariCore.sendChatMessageFormat("welcome", player,
//                ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME),
//                authPlayer.getPermGroup());

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDisconnect(PlayerQuitEvent event) {
        manager.removeAuthPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        AuthPlayer authPlayer = manager.getAuthPlayer(event.getPlayer().getUniqueId());
        if (authPlayer != null && !authPlayer.isLoggedIn()) {
            hikariCore.sendChatMessage("auth-restricted-action", event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event)
    {
        AuthPlayer authPlayer = manager.getAuthPlayer(event.getPlayer().getUniqueId());
        if (authPlayer != null && !authPlayer.isLoggedIn()) {
            hikariCore.sendChatMessage("auth-restricted-action", event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlaceEvent(BlockPlaceEvent event)
    {
        AuthPlayer authPlayer = manager.getAuthPlayer(event.getPlayer().getUniqueId());
        if (authPlayer != null && !authPlayer.isLoggedIn()) {
            hikariCore.sendChatMessage("auth-restricted-action", event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreakEvent(BlockBreakEvent event)
    {
        AuthPlayer authPlayer = manager.getAuthPlayer(event.getPlayer().getUniqueId());
        if (authPlayer != null && !authPlayer.isLoggedIn()) {
            hikariCore.sendChatMessage("auth-restricted-action", event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMoveEvent(PlayerMoveEvent event)
    {
        AuthPlayer authPlayer = manager.getAuthPlayer(event.getPlayer().getUniqueId());
        if (authPlayer != null && !authPlayer.isLoggedIn()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event)
    {
        AuthPlayer authPlayer = manager.getAuthPlayer(event.getPlayer().getUniqueId());
        if (authPlayer != null && !authPlayer.isLoggedIn()) {
            hikariCore.sendChatMessage("auth-restricted-action", event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItemEvent(EntityPickupItemEvent event)
    {
        if (!(event.getEntity() instanceof Player))
            return;

        AuthPlayer authPlayer = manager.getAuthPlayer(event.getEntity().getUniqueId());
        if (authPlayer != null && !authPlayer.isLoggedIn()) {
            hikariCore.sendChatMessage("auth-restricted-action", event.getEntity());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInventoryOpenEvent(InventoryOpenEvent event)
    {
        AuthPlayer authPlayer = manager.getAuthPlayer(event.getPlayer().getUniqueId());
        if (authPlayer != null && !authPlayer.isLoggedIn()) {
            hikariCore.sendChatMessage("auth-restricted-action", event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInventoryClickEvent(InventoryClickEvent event)
    {
        AuthPlayer authPlayer = manager.getAuthPlayer(event.getWhoClicked().getUniqueId());
        if (authPlayer != null && !authPlayer.isLoggedIn()) {
            hikariCore.sendChatMessage("auth-restricted-action", event.getWhoClicked());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        AuthPlayer authPlayer = manager.getAuthPlayer(event.getPlayer().getUniqueId());
        if (authPlayer != null && !authPlayer.isLoggedIn()) {
            hikariCore.sendChatMessage("auth-restricted-action", event.getPlayer());
            event.setCancelled(true);
        }
    }
}
