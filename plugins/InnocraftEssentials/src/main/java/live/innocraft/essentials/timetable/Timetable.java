package live.innocraft.essentials.timetable;

import live.innocraft.essentials.Essentials;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;

import java.util.HashSet;
import java.util.Set;

public class Timetable {

    private final Essentials plugin;
    private final Set<TimetableLesson> lessons;

    public Timetable(Essentials plugin) {
        this.plugin = plugin;
        this.lessons = new HashSet<TimetableLesson>();

        Reload();

        new TimetableCommands(plugin, this);
    }

    public void Reload() {
        plugin.GetConfiguration().ReloadTimetable();
        Configuration cfg = plugin.GetConfiguration().GetCfgTimetable();

        lessons.clear();

        Set<String> keys = cfg.getKeys(false);
        for (String key : keys) {
            TimetableLesson lesson = new TimetableLesson();
            if (cfg.contains(key + ".name"))
                lesson.Name = cfg.getString(key + ".name");
            if (cfg.contains(key + ".icon"))
                lesson.Icon = (Material)cfg.get(key + ".icon");
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
    }
}
