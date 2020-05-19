package live.innocraft.essentials.timetable;

import org.bukkit.Material;

public class TimetableLesson implements Comparable<TimetableLesson> {
    public String Name = "Lesson";
    public Material Icon = Material.ENCHANTED_BOOK;
    public int TimeStart = 0;
    public int TimeEnd = 0;
    public String Classroom = "";
    public boolean Compulsory = false;
    public boolean BlockCmds = false;
    public String Group = "";

    @Override
    public int compareTo(TimetableLesson other){
        if (TimeStart == other.TimeStart) {
            if (TimeEnd == other.TimeEnd) {
                if (Name.equals(other.Name)) {
                    if (Group.equals(other.Group))
                        return 1;
                    return Group.compareTo(other.Group);
                }
                return Name.compareTo(other.Name);
            }
            return TimeEnd < other.TimeEnd ? -1 : 1;
        }
        return TimeStart < other.TimeStart ? -1 : 1;
    }
}
