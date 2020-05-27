package live.innocraft.essentials;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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

        if (!label.equalsIgnoreCase("innocraft"))
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
        }

        return false;
    }

}
