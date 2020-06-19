package live.innocraft.essentials.common;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static org.bukkit.Bukkit.getServer;

public class EssentialsEvents extends EssentialsModule implements Listener {

    public EssentialsEvents(Essentials plugin) {
        super(plugin);
        getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onLateInitialization() {

    }

    @Override
    public void onReload() {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        event.setJoinMessage("");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
    }


}
