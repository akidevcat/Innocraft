package live.innocraft.essentials.ustudy;

import live.innocraft.essentials.bridge.Bridge;
import live.innocraft.essentials.timetable.Timetable;
import live.innocraft.essentials.timetable.TimetableLesson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UStudyUpdater implements Runnable {

    private final UStudy core;

    public UStudyUpdater(UStudy core) {
        this.core = core;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(core.getPlugin(), this, 0, 200);
    }

    @Override
    public void run() {
//        if (!core.getPlugin().getServerType().equals("general"))
//            return;
//
//        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
//        if (players.size() == 0)
//            return;
//        if (core.getPlugin().getModule(Timetable.class).isRestrictedModeEnabled())
//            core.getPlugin().getModule(Bridge.class).SendRestrictionInvalidationRequest(players.get(0));
    }

}
