package live.innocraft.essentials.common;

import live.innocraft.essentials.bridge.Bridge;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EssentialsCommands extends EssentialsModule implements CommandExecutor {

    public EssentialsCommands (Essentials plugin) {
        super(plugin);
        plugin.getCommand("innocraft").setExecutor(this);
    }

    @Override
    public void onLateInitialization() {

    }

    @Override
    public void onReload() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Alias check
        if (!label.equalsIgnoreCase("innocraft") && !label.equalsIgnoreCase("ic"))
            return false;

        if (args.length == 0) {
            getPlugin().getConfiguration().SendMessage("wrong-command-format", sender);
            return true;
        }

        switch (args[0]) {
            case "reload":
                if (!sender.hasPermission("innocraft.staff")) {
                    getPlugin().getConfiguration().SendMessage("permission-error", sender);
                    return true;
                }
                getPlugin().reloadAll();
                getPlugin().getConfiguration().SendMessage("plugin-reload", sender);
                return true;
            case "sync":
                if (!sender.hasPermission("innocraft.organizer")) {
                    getPlugin().getConfiguration().SendMessage("permission-error", sender);
                    return true;
                }
                getPlugin().syncAll();
                return true;
            case "join":
                if (args.length != 2) {
                    getPlugin().getConfiguration().SendMessage("wrong-command-format", sender);
                    return true;
                }
                if (!(sender instanceof Player)) {
                    getPlugin().getConfiguration().SendMessage("wrong-command-sender", sender);
                    return true;
                }
                if (!sender.hasPermission("innocraft.server." + args[1])) {
                    getPlugin().getConfiguration().SendMessage("permission-error", sender);
                    return true;
                }
                getPlugin().getModule(Bridge.class).ChangePlayerServer((Player)sender, args[1]);
                return true;
        }

        return false;
    }

}
