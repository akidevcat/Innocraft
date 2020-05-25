package live.innocraft.essentials.authkeys;

import live.innocraft.essentials.Essentials;
import live.innocraft.essentials.EssentialsHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class AuthKeysCommands implements CommandExecutor {

    //private HashMap<UUID, Long> playerCooldowns; ToDo
    private final Essentials plugin;

    public AuthKeysCommands (Essentials plugin) {
        this.plugin = plugin;
        //this.playerCooldowns = new HashMap<UUID, Long>();

        plugin.getCommand("redeem").setExecutor(this);
        plugin.getCommand("innocraft-authkey").setExecutor(this);
    }

    public boolean RedeemKey (Player player, String key) {
        FileConfiguration cfgAuthKeys = plugin.GetConfiguration().GetCfgAuthKeys();
        FileConfiguration cfgCommon = plugin.GetConfiguration().GetCfgCommon();
        String keyHash = EssentialsHelper.HashSHA256(key);
        Set<String> cfgKeys = cfgAuthKeys.getKeys(false);
        Set<String> groups = cfgCommon.getConfigurationSection("auth-keys.groups").getKeys(false);

        if (!cfgKeys.contains(keyHash)) {
            plugin.GetConfiguration().SendMessage("auth-key-wrong", player);
            return false;
        }

        String keyGroup = cfgAuthKeys.getString(keyHash);

        //Check if the config contains this group
        if (!groups.contains(keyGroup)) {
            plugin.GetConfiguration().SendMessage("auth-key-wrong-group", player);
            return false;
        }

        //Clear the key from the config file and save it
        cfgAuthKeys.set(keyHash, null);
        plugin.GetConfiguration().SaveAuthKeys();

        //Execute all the group commands
        for (String command : cfgCommon.getStringList("auth-keys.groups." + keyGroup + ".commands")) {
            String parsedCommand = command.replaceAll("\\{player\\}", player.getName());
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), parsedCommand);
        }

        //Send a message to the player
        plugin.GetConfiguration().SendMessage("auth-key-redeemed", player);

        return true;
    }

    public void AddKey (String key, String group) {
        if (key.length() <= 3)
            return;

        String keyHash = EssentialsHelper.HashSHA256(key);
        FileConfiguration cfg = plugin.GetConfiguration().GetCfgAuthKeys();

        cfg.set(keyHash, group);
        plugin.GetConfiguration().SaveAuthKeys();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Redeem command section
        if (label.equalsIgnoreCase("redeem")) {
            if (!(sender instanceof Player))
                return false;
            Player player = (Player)sender;
            if (args.length != 1) {
                plugin.GetConfiguration().SendMessage("wrong-command-format", player);
                return true;
            }
            RedeemKey(player, args[0]);

            // Log this action
            plugin.GetConfiguration().LogAuthKeys("info", "An auth key was redeemed");
            plugin.GetConfiguration().LogAuthKeys("who", sender.getName());
            plugin.GetConfiguration().LogAuthKeys("key-hash", EssentialsHelper.HashSHA256(args[0]));

            return true;
        }

        if (!label.equalsIgnoreCase("innocraft-authkey"))
            return false;

        //innocraft-authkey command section
        if (args.length == 0) {
            plugin.GetConfiguration().SendMessage("wrong-command-format", sender);
            return true;
        }

        switch (args[0]) {
            case "add":
                if (args.length != 1 + 2 || args[1].length() <= 3) { //Requires 2 args and key length should be at least 4 chars
                    plugin.GetConfiguration().SendMessage("wrong-command-format", sender);
                    return true;
                }

                if (!sender.hasPermission("innocraft.organizer")) {
                    plugin.GetConfiguration().SendMessage("permission-error", sender);
                    return true;
                }

                AddKey(args[1], args[2]);
                plugin.GetConfiguration().SendMessage("auth-key-added", sender);

                // Log this action
                plugin.GetConfiguration().LogAuthKeys("info", "An auth key was created");
                plugin.GetConfiguration().LogAuthKeys("who", sender.getName());
                plugin.GetConfiguration().LogAuthKeys("key-hash", EssentialsHelper.HashSHA256(args[1]));
                plugin.GetConfiguration().LogAuthKeys("group", args[2], true);

                return true;
            case "reload":

                if (!sender.hasPermission("innocraft.staff")) {
                    plugin.GetConfiguration().SendMessage("permission-error", sender);
                    return true;
                }

                plugin.GetConfiguration().ReloadAuthKeys();
                plugin.GetConfiguration().SendMessage("auth-key-reloaded", sender);
                return true;
        }

        return true;
    }

}
