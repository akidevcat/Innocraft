package live.innocraft.essentials.authkeys;

import live.innocraft.essentials.Essentials;
import live.innocraft.essentials.EssentialsHelper;
import live.innocraft.essentials.EssentialsModule;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class AuthKeysCommands extends EssentialsModule implements CommandExecutor {

    public AuthKeysCommands (Essentials plugin) {
        super(plugin);
        //this.playerCooldowns = new HashMap<UUID, Long>();

        plugin.getCommand("redeem").setExecutor(this);
        plugin.getCommand("innocraft-authkey").setExecutor(this);
    }

    @Override
    public void Reload() {

    }

    public boolean RedeemKey (Player player, String key) {
        FileConfiguration cfgAuthKeys = getPlugin().GetConfiguration().GetCfgAuthKeys();
        FileConfiguration cfgCommon = getPlugin().GetConfiguration().GetCfgCommon();
        String keyHash = EssentialsHelper.HashSHA256(key);
        Set<String> cfgKeys = cfgAuthKeys.getKeys(false);
        Set<String> groups = cfgCommon.getConfigurationSection("auth-keys.groups").getKeys(false);

        if (!cfgKeys.contains(keyHash)) {
            getPlugin().GetConfiguration().SendMessage("auth-key-wrong", player);
            return false;
        }

        String keyGroup = cfgAuthKeys.getString(keyHash);

        // Log this action
        getPlugin().GetConfiguration().LogAuthKeys("info", "An auth key was redeemed");
        getPlugin().GetConfiguration().LogAuthKeys("who", player.getName());
        getPlugin().GetConfiguration().LogAuthKeys("key-hash", keyHash);

        //Check if the config contains this group
        if (!groups.contains(keyGroup)) {
            getPlugin().GetConfiguration().SendMessage("auth-key-wrong-group", player);
            return false;
        }

        //Clear the key from the config file and save it
        cfgAuthKeys.set(keyHash, null);
        getPlugin().GetConfiguration().SaveAuthKeys();

        //Execute all the group commands
        for (String command : cfgCommon.getStringList("auth-keys.groups." + keyGroup + ".commands")) {
            String parsedCommand = command.replaceAll("\\{player\\}", player.getName());
            getPlugin().getServer().dispatchCommand(getPlugin().getServer().getConsoleSender(), parsedCommand);
        }

        //Send a message to the player
        getPlugin().GetConfiguration().SendMessage("auth-key-redeemed", player);

        return true;
    }

    public void AddKey (String key, String group, String logName) {
        if (key.length() <= 3)
            return;

        String keyHash = EssentialsHelper.HashSHA256(key);
        FileConfiguration cfg = getPlugin().GetConfiguration().GetCfgAuthKeys();

        cfg.set(keyHash, group);
        getPlugin().GetConfiguration().SaveAuthKeys();

        // Log this action
        getPlugin().GetConfiguration().LogAuthKeys("info", "An auth key was created");
        getPlugin().GetConfiguration().LogAuthKeys("who", logName);
        getPlugin().GetConfiguration().LogAuthKeys("key-hash", keyHash);
        getPlugin().GetConfiguration().LogAuthKeys("group", group, true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Redeem command section
        if (label.equalsIgnoreCase("redeem")) {
            if (!(sender instanceof Player))
                return false;
            Player player = (Player)sender;
            if (args.length != 1) {
                getPlugin().GetConfiguration().SendMessage("wrong-command-format", player);
                return true;
            }
            RedeemKey(player, args[0]);

            return true;
        }

        if (!label.equalsIgnoreCase("innocraft-authkey"))
            return false;

        //innocraft-authkey command section
        if (args.length == 0) {
            getPlugin().GetConfiguration().SendMessage("wrong-command-format", sender);
            return true;
        }

        switch (args[0]) {
            case "add":
                if (args.length != 1 + 2 || args[1].length() <= 3) { //Requires 2 args and key length should be at least 4 chars
                    getPlugin().GetConfiguration().SendMessage("wrong-command-format", sender);
                    return true;
                }

                if (!sender.hasPermission("innocraft.organizer")) {
                    getPlugin().GetConfiguration().SendMessage("permission-error", sender);
                    return true;
                }

                AddKey(args[1], args[2], sender.getName());
                getPlugin().GetConfiguration().SendMessage("auth-key-added", sender);

                return true;
            case "reload":

                if (!sender.hasPermission("innocraft.staff")) {
                    getPlugin().GetConfiguration().SendMessage("permission-error", sender);
                    return true;
                }

                getPlugin().GetConfiguration().ReloadAuthKeys();
                getPlugin().GetConfiguration().SendMessage("auth-key-reloaded", sender);
                return true;
        }

        return true;
    }

}
