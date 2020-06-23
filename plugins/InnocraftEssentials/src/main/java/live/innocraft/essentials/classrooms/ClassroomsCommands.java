package live.innocraft.essentials.classrooms;

import live.innocraft.essentials.core.Essentials;
import live.innocraft.essentials.helper.EssentialsHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClassroomsCommands implements CommandExecutor {

    private final Essentials plugin;
    private final Classrooms classrooms;

    public ClassroomsCommands(Essentials plugin, Classrooms classrooms) {
        this.plugin = plugin;
        this.classrooms = classrooms;

        plugin.getCommand("classrooms").setExecutor(this);
        plugin.getCommand("classes").setExecutor(this);
        plugin.getCommand("rooms").setExecutor(this);
        plugin.getCommand("innocraft-classrooms").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Alias section
//        switch (label) {
//            case "classrooms":
//            case "classes":
//            case "rooms":
//                label = "innocraft-classrooms";
//                break;
//        }
//
//        if (!label.equalsIgnoreCase("innocraft-classrooms"))
//            return false;
//
//        //innocraft-classrooms command section
//        if (args.length == 0) {
//            plugin.getConfiguration().SendMessage("wrong-command-format", sender);
//            return true;
//        }
//
//        switch (args[0]) {
//            case "menu":
//
//                break;
//            case "create":
//            case "new":
//                if (!sender.hasPermission("innocraft.organizer")) {
//                    plugin.getConfiguration().SendMessage("permission-error", sender);
//                    return true;
//                }
//                if (args.length != 1 + 1) {
//                    plugin.getConfiguration().SendMessage("wrong-command-format", sender);
//                    return true;
//                }
//                if (!classrooms.CreateClassroom(args[1])) {
//                    plugin.getConfiguration().SendMessage("classroom-creation-exists", sender);
//                    return true;
//                }
//                plugin.getConfiguration().SendMessage("classroom-creation-success", sender);
//                return true;
//            case "delete":
//            case "remove":
//                if (!sender.hasPermission("innocraft.organizer")) {
//                    plugin.getConfiguration().SendMessage("permission-error", sender);
//                    return true;
//                }
//                if (args.length != 1 + 1) {
//                    plugin.getConfiguration().SendMessage("wrong-command-format", sender);
//                    return true;
//                }
//                if (!classrooms.DeleteClassroom(args[1])) {
//                    plugin.getConfiguration().SendMessage("classroom-missing", sender);
//                    return true;
//                }
//                plugin.getConfiguration().SendMessage("classroom-deletion-success", sender);
//                return true;
//            case "set-name":
//            case "set-displayedname":
//                if (!sender.hasPermission("innocraft.organizer")) {
//                    plugin.getConfiguration().SendMessage("permission-error", sender);
//                    return true;
//                }
//                if (args.length != 1 + 2) {
//                    plugin.getConfiguration().SendMessage("wrong-command-format", sender);
//                    return true;
//                }
//                if (!classrooms.SetClassroomDisplayedName(args[1], args[2])) {
//                    plugin.getConfiguration().SendMessage("classroom-missing", sender);
//                    return true;
//                }
//                plugin.getConfiguration().SendMessage("classroom-setname-success", sender);
//                return true;
//            case "set-region":
//            case "set-rg":
//                if (!sender.hasPermission("innocraft.organizer")) {
//                    plugin.getConfiguration().SendMessage("permission-error", sender);
//                    return true;
//                }
//                if (!(sender instanceof Player)) {
//                    plugin.getConfiguration().SendMessage("wrong-command-sender", sender);
//                    return true;
//                }
//
//                // Get the exact region
//                String region = "";
//
//                if (args.length == 1 + 2) {
//                    region = args[2];
//                } else if (args.length == 1 + 1) {
//                    String[] regions = EssentialsHelper.GetRegionsNames((Player)sender);
//                    if (regions.length == 1) {
//                        region = regions[0];
//                    } else if (regions.length > 1) {
//                        plugin.getConfiguration().SendMessage("classroom-setregion-conflict", sender);
//                        return true;
//                    }
//                } else {
//                    plugin.getConfiguration().SendMessage("wrong-command-format", sender);
//                    return true;
//                }
//
//                // In case there's no region
//                if (region.equals("")) {
//                    plugin.getConfiguration().SendMessage("classroom-setregion-missing", sender);
//                    return true;
//                }
//                if (!classrooms.SetClassroomRegion(args[1], region)) {
//                    plugin.getConfiguration().SendMessage("classroom-missing", sender);
//                    return true;
//                }
//                plugin.getConfiguration().SendMessage("classroom-setregion-success", sender);
//                return true;
//            case "rm-region":
//            case "remove-region":
//            case "dl-region":
//            case "delete-region":
//            case "rm-rg":
//            case "dl-rg":
//                if (!sender.hasPermission("innocraft.organizer")) {
//                    plugin.getConfiguration().SendMessage("permission-error", sender);
//                    return true;
//                }
//                if (args.length != 1 + 1) {
//                    plugin.getConfiguration().SendMessage("wrong-command-format", sender);
//                    return true;
//                }
//                if (!classrooms.SetClassroomRegion(args[1], "")) {
//                    plugin.getConfiguration().SendMessage("classroom-missing", sender);
//                    return true;
//                }
//                plugin.getConfiguration().SendMessage("classroom-setregion-success", sender);
//                return true;
//            case "set-position":
//            case "set-location":
//            case "set-pos":
//                if (!sender.hasPermission("innocraft.organizer")) {
//                    plugin.getConfiguration().SendMessage("permission-error", sender);
//                    return true;
//                }
//                if (!(sender instanceof Player)) {
//                    plugin.getConfiguration().SendMessage("wrong-command-sender", sender);
//                    return true;
//                }
//                if (args.length != 1 + 1) {
//                    plugin.getConfiguration().SendMessage("wrong-command-format", sender);
//                    return true;
//                }
//                if (!classrooms.SetClassroomPosition(args[1], ((Player) sender).getLocation())) {
//                    plugin.getConfiguration().SendMessage("classroom-missing", sender);
//                    return true;
//                }
//                plugin.getConfiguration().SendMessage("classroom-setposition-success", sender);
//                return true;
//            case "set-link":
//                if (!sender.hasPermission("innocraft.organizer")) {
//                    plugin.getConfiguration().SendMessage("permission-error", sender);
//                    return true;
//                }
//                if (args.length != 1 + 2 && args.length != 1 + 3) {
//                    plugin.getConfiguration().SendMessage("wrong-command-format", sender);
//                    return true;
//                }
//                if (!classrooms.SetClassroomLink(args[1], args[2])) {
//                    plugin.getConfiguration().SendMessage("classroom-missing", sender);
//                    return true;
//                }
//                if (args.length == 1 + 3) {
//                    classrooms.SetClassroomCode(args[1], args[3]);
//                } else {
//                    classrooms.SetClassroomCode(args[1], "");
//                }
//                plugin.getConfiguration().SendMessage("classroom-setlink-success", sender);
//                return true;
//            case "rm-link":
//            case "dl-link":
//            case "remove-link":
//            case "delete-link":
//                if (!sender.hasPermission("innocraft.organizer")) {
//                    plugin.getConfiguration().SendMessage("permission-error", sender);
//                    return true;
//                }
//                if (args.length != 1 + 1) {
//                    plugin.getConfiguration().SendMessage("wrong-command-format", sender);
//                    return true;
//                }
//                if (!classrooms.SetClassroomLink(args[1], "")) {
//                    plugin.getConfiguration().SendMessage("classroom-missing", sender);
//                    return true;
//                }
//                plugin.getConfiguration().SendMessage("classroom-setlink-success", sender);
//                return true;
//            case "get-link":
//            case "link":
//                if (!sender.hasPermission("innocraft.participant")) {
//                    plugin.getConfiguration().SendMessage("permission-error", sender);
//                    return true;
//                }
//                if (!(sender instanceof Player)) {
//                    plugin.getConfiguration().SendMessage("wrong-command-sender", sender);
//                    return true;
//                }
//                if (args.length != 1) {
//                    plugin.getConfiguration().SendMessage("wrong-command-format", sender);
//                    return true;
//                }
//                String auditorium = classrooms.GetClassroomByPlayer((Player)sender);
//                if (auditorium.equals("")) {
//                    plugin.getConfiguration().SendMessage("classroom-region-missing", sender);
//                    return true;
//                }
//                String link = classrooms.GetClassroomLink(auditorium);
//                if (link.equals("")) {
//                    plugin.getConfiguration().SendMessage("classroom-link-missing", sender);
//                    return true;
//                }
//                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + sender.getName() +
//                        " {\"text\":\"" + plugin.getConfiguration().GetMessage("classroom-link-format") +
//                        "\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + link +"\"}}");
//                String code = classrooms.GetClassroomCode(auditorium);
//                if (!code.equals("")) {
//                    plugin.getConfiguration().SendMessageFormat("classroom-code-format", sender, code);
//                }
//                return true;
//        }

        return true;
    }

}
