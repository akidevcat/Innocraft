package live.innocraft.essentials.timetable;

import live.innocraft.essentials.Essentials;
import live.innocraft.essentials.classrooms.Classrooms;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimetableCommands implements CommandExecutor {

    private final Essentials plugin;
    private final Timetable timetable;

    public TimetableCommands(Essentials plugin, Timetable timetable) {
        this.plugin = plugin;
        this.timetable = timetable;

        plugin.getCommand("timetable").setExecutor(this);
        plugin.getCommand("innocraft-timetable").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Alias section
        switch (label) {
            case "timetable":
                label = "innocraft-timetable";
                break;
        }

        if (!label.equalsIgnoreCase("innocraft-timetable"))
            return false;

        //innocraft-timetable command section
        if (args.length == 0) {
            plugin.GetConfiguration().SendMessage("wrong-command-format", sender);
            return true;
        }

        switch (args[0]) {
            case "menu":
                if (!(sender instanceof Player)) {
                    plugin.GetConfiguration().SendMessage("wrong-command-sender", sender);
                    return true;
                }
                if (!sender.hasPermission("innocraft.participant")) {
                    plugin.GetConfiguration().SendMessage("permission-error", sender);
                    return true;
                }
                timetable.OpenGUI((Player)sender);
                return true;
        }

        return true;
    }

}
