package live.innocraft.essentials;

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
    public void LateInitialization() {

    }

    @Override
    public void Reload() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Alias check
        if (!label.equalsIgnoreCase("innocraft") && !label.equalsIgnoreCase("ic"))
            return false;

        if (args.length == 0) {
            getPlugin().GetConfiguration().SendMessage("wrong-command-format", sender);
            return true;
        }

        switch (args[0]) {
            case "reload":
                if (!sender.hasPermission("innocraft.staff")) {
                    getPlugin().GetConfiguration().SendMessage("permission-error", sender);
                    return true;
                }
                getPlugin().ReloadAll();
                getPlugin().GetConfiguration().SendMessage("plugin-reload", sender);
                return true;
            case "sync":
                if (!sender.hasPermission("innocraft.organizer")) {
                    getPlugin().GetConfiguration().SendMessage("permission-error", sender);
                    return true;
                }
                getPlugin().SyncAll();
                return true;
            case "join":
                if (args.length != 2) {
                    getPlugin().GetConfiguration().SendMessage("wrong-command-format", sender);
                    return true;
                }
                if (!(sender instanceof Player)) {
                    getPlugin().GetConfiguration().SendMessage("wrong-command-sender", sender);
                    return true;
                }
                if (!sender.hasPermission("innocraft.server." + args[1])) {
                    getPlugin().GetConfiguration().SendMessage("permission-error", sender);
                    return true;
                }
                getPlugin().getModule(Bridge.class).ChangePlayerServer((Player)sender, args[1]);
                return true;
        }

        return false;
    }

}
