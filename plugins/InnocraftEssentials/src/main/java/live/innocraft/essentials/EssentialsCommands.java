package live.innocraft.essentials;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EssentialsCommands implements CommandExecutor {

    private Essentials plugin;

    public EssentialsCommands (Essentials plugin) {
        this.plugin = plugin;

        plugin.getCommand("innocraft").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Alias check

        if (!label.equalsIgnoreCase("innocraft"))
            return false;

        if (args.length == 0) {
            plugin.GetConfiguration().SendMessage("wrong-command-format", sender);
            return true;
        }

        switch (args[0]) {
            case "reload":
                if (!sender.hasPermission("innocraft.staff")) {
                    plugin.GetConfiguration().SendMessage("permission-error", sender);
                    return true;
                }
                plugin.GetConfiguration().ReloadAll();
                plugin.GetConfiguration().SendMessage("plugin-reload", sender);
                return true;
        }

        return false;
    }

}
