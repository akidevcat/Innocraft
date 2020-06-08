package live.innocraft.essentials.ustudy;

import live.innocraft.essentials.Essentials;
import live.innocraft.essentials.EssentialsModule;
import live.innocraft.essentials.timetable.Timetable;

import java.util.HashSet;

public class UStudy extends EssentialsModule {

    private Timetable timetable;
    private final HashSet<Integer> webhookMessageHashes;

    public UStudy (Essentials plugin) {
        super(plugin);
        webhookMessageHashes = new HashSet<>();
    }

    @Override
    public void LateInitialization() {
        timetable = getPlugin().getModule(Timetable.class);

        new UStudyUpdater(this);
        //getPlugin().getModule(Discord.class).SendTimetable(getPlugin().getModule(Timetable.class).getLessons());
        //getPlugin().getModule(Discord.class).SendClassesWebhook(getPlugin().getModule(Timetable.class).getCurrentLesson());
    }

    @Override
    public void Reload() {

    }
}
