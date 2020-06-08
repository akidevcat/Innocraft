package live.innocraft.essentials.timetable;

import live.innocraft.essentials.Essentials;
import live.innocraft.essentials.helper.EssentialsHelper;
import live.innocraft.essentials.EssentialsModule;
import live.innocraft.essentials.classrooms.Classrooms;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

public class Timetable extends EssentialsModule {

    private final Classrooms classrooms;
    private final TreeSet<TimetableLesson> lessons;
    private final TimetableGUI gui;

    public Timetable(Essentials plugin) {
        super(plugin);
        this.lessons = new TreeSet<TimetableLesson>();
        this.gui = new TimetableGUI(plugin, this);
        this.classrooms = plugin.getModule(Classrooms.class);

        Reload();
    }

    @Override
    public void LateInitialization() {
        new TimetableCommands(getPlugin(), this);
    }

    @Override
    public void Reload() {
        getPlugin().GetConfiguration().ReloadTimetable();
        Configuration cfg = getPlugin().GetConfiguration().GetCfgTimetable();

        lessons.clear();

        Set<String> keys = cfg.getKeys(false);
        for (String key : keys) {
            TimetableLesson lesson = new TimetableLesson();
            if (cfg.contains(key + ".name"))
                lesson.Name = cfg.getString(key + ".name");
            if (cfg.contains(key + ".icon"))
                lesson.Icon = Material.valueOf(cfg.getString(key + ".icon"));
            if (cfg.contains(key + ".start"))
                lesson.TimeStart = cfg.getInt(key + ".start");
            if (cfg.contains(key + ".end"))
                lesson.TimeEnd = cfg.getInt(key + ".end");
            if (cfg.contains(key + ".classroom"))
                lesson.Classroom = cfg.getString(key + ".classroom");
            if (cfg.contains(key + ".compulsory"))
                lesson.Compulsory = cfg.getBoolean(key + ".compulsory");
            if (cfg.contains(key + ".block-cmds"))
                lesson.BlockCmds = cfg.getBoolean(key + ".block-cmds");
            if (cfg.contains(key + ".group"))
                lesson.Group = cfg.getString(key + ".group");
            lessons.add(lesson);
        }

        gui.Update(lessons);
    }

    @Override
    public void Sync() {
        getPlugin().GetConfiguration().SyncTimetableCfg();
        Reload();
    }

    public @Nullable TimetableLesson getCurrentLesson() {
        LocalDateTime date = LocalDateTime.now();
        int minutes = date.toLocalTime().toSecondOfDay() / 60;
        getPlugin().getLogger().log(Level.SEVERE, "MINUTES: " + minutes);
        for (TimetableLesson lesson : lessons) {
            getPlugin().getLogger().log(Level.SEVERE, "START: " + lesson.TimeStart + " END: " + lesson.TimeEnd);
            if (minutes >= lesson.TimeStart && minutes <= lesson.TimeEnd)
                return lesson;
        }
        return null;
    }

    public ArrayList<TimetableLesson> getCurrentLessons() {
        ArrayList<TimetableLesson> result = new ArrayList<>();
        LocalDateTime date = LocalDateTime.now();
        int minutes = date.toLocalTime().toSecondOfDay() / 60;
        for (TimetableLesson lesson : lessons) {
            if (minutes >= lesson.TimeStart && minutes <= lesson.TimeEnd)
                result.add(lesson);
        }
        return result;
    }

    public @Nullable TimetableLesson getCurrentLesson(Player player) {
        LocalDateTime date = LocalDateTime.now();
        int minutes = date.toLocalTime().toSecondOfDay() / 60;
        for (TimetableLesson lesson : lessons) {
            if (minutes >= lesson.TimeStart && minutes <= lesson.TimeEnd) {
                if (!lesson.Group.equals("") && !player.hasPermission("innocraft.group." + lesson.Group))
                    continue;
                return lesson;
            }
        }
        return null;
    }

    public boolean isRestrictedModeEnabled() {
        LocalDateTime date = LocalDateTime.now();
        int minutes = date.toLocalTime().toSecondOfDay() / 60;
        for (TimetableLesson lesson : lessons) {
            if (minutes >= lesson.TimeStart && minutes <= lesson.TimeEnd && lesson.Compulsory)
                return true;
        }
        return false;
    }

    public TreeSet<TimetableLesson> getLessons() {
        return lessons;
    }

    public void OpenGUI(final HumanEntity ent) {
        gui.Open(ent);
    }

    public String GetGUIName() {
        return getPlugin().GetConfiguration().GetCfgCommon().getString("timetable.gui-title");
    }

    public List<String> GetGUILoreFormat() {
        return getPlugin().GetConfiguration().GetCfgCommon().getStringList("timetable.gui-lesson-description-format");
    }

    public String ApplyPlaceholders(String s, TimetableLesson l) {
        int replaced = 0;
        boolean empty = false;
        if (s.contains("%start%")) {
            s = s.replaceAll("%start%", EssentialsHelper.ConvertMinutesToTimeString(l.TimeStart));
            replaced++;
        }
        if (s.contains("%end%")) {
            s = s.replaceAll("%end%", EssentialsHelper.ConvertMinutesToTimeString(l.TimeEnd));
            replaced++;
        }
        if (s.contains("%classroom%")) {
            if (l.Classroom.equals(""))
                empty = true;
            else
                s = s.replaceAll("%classroom%", classrooms.GetClassroomDisplayedName(l.Classroom));
            replaced++;
        }
        if (s.contains("%compulsory%")) {
            if (!l.Compulsory)
                empty = true;
            else
                s = s.replaceAll("%compulsory%", "");
            replaced++;
        }
        if (s.contains("%group%")) {
            if (l.Group.equals(""))
                empty = true;
            else
                s = s.replaceAll("%group%", l.Group);
            replaced++;
        }

        // In case there's only one placeholder and it's empty - return skip string
        if (replaced == 1 && empty)
            return "%skip%";

        return s;
    }
}
