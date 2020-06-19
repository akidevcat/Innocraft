package live.innocraft.doorsopenday;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class DoorsOpenDay extends JavaPlugin implements Listener {

    public static final int CONST_START_BALANCE = 1000;
    public static final int CONST_MERCH_FINE = 300;
    public static final float CONST_MERCH_FINE_CHANCE = 0.7f;
    public static final int CONST_START_TIME = 30 * 60;
    public static final int CONST_PASS_SCORE = 10;
    public static final int CONST_FOOD_BONUS = 10 * 60;

    protected HashMap<Player, Participant> participants = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("dod").setExecutor(new DODCommands(this));
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.getPlayer().setAllowFlight(false);
            event.getPlayer().setFlying(false);
        }
        event.getPlayer().getInventory().clear();
        event.getPlayer().teleport(new Location(event.getPlayer().getWorld(),73, 37, 142));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        participants.remove(event.getPlayer());
    }

    public void removeParticipant(Player p) {
        if (!participants.containsKey(p))
            return;
        Participant pt = participants.get(p);
        pt.destruct();
        participants.remove(p);
        p.getInventory().clear();
    }

    public Participant addParticipant(Player p, String lang) {
        Participant pt = new Participant(this, p, lang);
        pt.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, pt, 0L, 20L);
        participants.put(p, pt);
        return pt;
    }
}
