package live.innocraft.hikari.Common;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HikariPlayerManagerEvents implements Listener {

    private final HikariPlayerManager module;

    public HikariPlayerManagerEvents(HikariPlayerManager module) {
        this.module = module;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin (PlayerJoinEvent event) {
        module.createHikariPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit (PlayerQuitEvent event) {
        module.removeHikariPlayer(event.getPlayer().getUniqueId());
    }

}
