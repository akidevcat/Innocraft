package live.innocraft.smbridge;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class PluginCommands extends Command {

    private final SMBridge main;

    public PluginCommands(SMBridge This) {
        super("smbridge", "smbridge.admin");
        main = This;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length != 1 || !args[0].equals("reload")) {
            commandSender.sendMessage(new TextComponent(ChatColor.BLUE + "SMBridge Plugin"));
            commandSender.sendMessage(new TextComponent(ChatColor.BLUE + "/smbridge reload - Reloads config files"));
            return;
        }
        main.reloadConfigs();
        commandSender.sendMessage(new TextComponent(ChatColor.GREEN + "SMBridge Config files were reloaded!"));
    }

}
