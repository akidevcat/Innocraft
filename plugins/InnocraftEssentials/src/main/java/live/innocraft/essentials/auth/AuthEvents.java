package live.innocraft.essentials.auth;

import live.innocraft.essentials.authkeys.AuthKeys;
import live.innocraft.essentials.common.ServerType;
import live.innocraft.essentials.discord.Discord;
import live.innocraft.essentials.helper.EssentialsHelper;
import live.innocraft.essentials.sql.EssentialsSQL;
import me.spomg.minecord.Minecord;
import me.spomg.minecord.api.MAPI;
import me.stefan911.securitymaster.lite.api.events.player.PlayerLoginEvent;
import me.stefan911.securitymaster.lite.api.events.player.PlayerRegisterEvent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

public class AuthEvents implements Listener {

    private final Auth auth;

    public AuthEvents(Auth auth) {
        this.auth = auth;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onConnect(PlayerJoinEvent event) {
        AuthPlayer authPlayer = auth.addAuthPlayer(event.getPlayer().getUniqueId());
        if (!EssentialsHelper.isNicknameValid(event.getPlayer().getName())) {
            event.getPlayer().kickPlayer(auth.getPlugin().getMessageColor("invalid-nickname-kick", "auth", authPlayer.getLanguage()));
            return;
        }
        if (!authPlayer.isRegistered()) {
            event.getPlayer().kickPlayer(auth.getPlugin().getMessageColorFormat("registration-kick", "auth", authPlayer.getLanguage(), authPlayer.getRegistrationCode()));
            return;
        }
        if (auth.getPlugin().getServerType() == ServerType.auth)
            auth.getPlugin().sendChatMessageFormatLang("login-request", event.getPlayer(), authPlayer.getLanguage(), event.getPlayer().getDisplayName());
    }

    protected void onLogin(AuthPlayer authPlayer) {

        Player player = Bukkit.getPlayer(authPlayer.getUniqueID());

        // Sync player language
        authPlayer.setLanguage(player.getLocale());
        auth.getPlugin().getModule(EssentialsSQL.class).setAuthPlayerLang(authPlayer.getUniqueID(), player.getLocale());

        // Sync auth key
        switch (auth.getModule(AuthKeys.class).syncOnlinePlayerAuthKey(authPlayer, authPlayer.getKeyHash())) {
            case 0:
                auth.getPlugin().sendChatMessage("key-synced", player);
                break;
            case 1:
                //auth.getPlugin().sendChatMessage("key-synced-invalid", player);
                break;
            case 2:
                auth.getPlugin().kickPlayerSync(player, auth.getPlugin().getMessageColor("key-expired-kick", "auth", authPlayer.getLanguage()));
                //player.kickPlayer(auth.getPlugin().getMessageColor("key-expired-kick", "auth", authPlayer.getLanguage()));
                break;
        }

        //Register to Minecord
        if (auth.getPlugin().hasDependency("Minecord")) {
            try {
                MAPI minecord = auth.getPlugin().getDependency(MAPI.class);
                Discord discord = auth.getModule(Discord.class);
                Member member = discord.getGuild().getMemberById(authPlayer.getDiscordID());
                minecord.link(player, member);
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        }

        // Send welcome message
        auth.getPlugin().sendChatMessageFormat("welcome", player,
                ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME),
                authPlayer.getPermGroup());

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
    public void onPlayerCommand(PlayerCommandPreprocessEvent event)
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        AuthPlayer authPlayer = auth.getAuthPlayer(event.getPlayer().getUniqueId());
        if (authPlayer != null && !authPlayer.isLoggedIn()) {
            auth.getPlugin().sendChatMessage("auth-restricted-action", event.getPlayer());
            event.setCancelled(true);
        }
    }
}
