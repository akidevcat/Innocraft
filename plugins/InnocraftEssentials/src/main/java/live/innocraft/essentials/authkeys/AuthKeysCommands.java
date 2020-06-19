package live.innocraft.essentials.authkeys;

import live.innocraft.essentials.common.Essentials;
import live.innocraft.essentials.discord.Discord;
import live.innocraft.essentials.helper.EssentialsHelper;
import live.innocraft.essentials.common.EssentialsModule;
import me.stefan911.securitymaster.lite.utils.account.AccountManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Set;

public class AuthKeysCommands extends EssentialsModule implements CommandExecutor {

    public AuthKeysCommands (Essentials plugin) {
        super(plugin);
        plugin.getCommand("redeem").setExecutor(this);
        plugin.getCommand("innocraft-authkey").setExecutor(this);
    }

    @Override
    public void onLateInitialization() {

    }

    @Override
    public void onReload() {

    }

    public boolean RedeemKey (Player player, String key) {
        FileConfiguration cfgAuthKeys = getPlugin().getConfiguration().GetCfgAuthKeys();
        FileConfiguration cfgCommon = getPlugin().getConfiguration().GetCfgCommon();
        String keyHash = EssentialsHelper.HashSHA256(key);
        Set<String> cfgKeys = cfgAuthKeys.getKeys(false);
        Set<String> groups = cfgCommon.getConfigurationSection("auth-keys.groups").getKeys(false);

        if (!cfgKeys.contains(keyHash)) {
            getPlugin().getConfiguration().SendMessage("auth-key-wrong", player);
            return false;
        }

        String keyGroup = cfgAuthKeys.getString(keyHash);

        // Log this action
        String logTime = EssentialsHelper.GetTimeStamp();
        getPlugin().getConfiguration().LogAuthKeys("info", "An auth key was redeemed", logTime);
        getPlugin().getConfiguration().LogAuthKeys("who", player.getName(), logTime);
        getPlugin().getConfiguration().LogAuthKeys("key-hash", keyHash, logTime, true);

        //Check if the config contains this group
        if (!groups.contains(keyGroup)) {
            getPlugin().getConfiguration().SendMessage("auth-key-wrong-group", player);
            return false;
        }

        //Clear the key from the config file and save it
        cfgAuthKeys.set(keyHash, null);
        getPlugin().getConfiguration().SaveAuthKeys();

        //Execute all the group commands
        for (String command : cfgCommon.getStringList("auth-keys.groups." + keyGroup + ".commands")) {
            String parsedCommand = command.replaceAll("\\{player\\}", player.getName());
            getPlugin().getServer().dispatchCommand(getPlugin().getServer().getConsoleSender(), parsedCommand);
        }

        if (cfgCommon.contains("auth-keys.groups." + keyGroup + ".role")) {
            //SecurityMasterAPI smapi = getPlugin().getDependency(SecurityMasterAPI.class);
            String discordID = (new AccountManager(player.getUniqueId())).getDiscordID();
            //String discordID = getPlugin().getModule(Auth.class).getDiscordID(player);
            String roleID = cfgCommon.getString("auth-keys.groups." + keyGroup + ".role");
            if (discordID == null) {
                getPlugin().getConfiguration().SendMessage("auth-key-discord-problem", player);
                getPlugin().getConfiguration().LogAuthKeys("role", "Unable to set role - player is not registered", logTime, true);
            }
            else {
                getPlugin().getModule(Discord.class).AddUserRole(discordID, roleID);
            }
        }

        //Send a message to the player
        getPlugin().getConfiguration().SendMessage("auth-key-redeemed", player);

        return true;
    }

    public void AddKey (String key, String group, String logName) {
        if (key.length() <= 3)
            return;

        String keyHash = EssentialsHelper.HashSHA256(key);
        FileConfiguration cfg = getPlugin().getConfiguration().GetCfgAuthKeys();

        cfg.set(keyHash, group);
        getPlugin().getConfiguration().SaveAuthKeys();

        // Log this action
        String logTime = EssentialsHelper.GetTimeStamp();
        getPlugin().getConfiguration().LogAuthKeys("info", "An auth key was created", logTime);
        getPlugin().getConfiguration().LogAuthKeys("who", logName, logTime);
        getPlugin().getConfiguration().LogAuthKeys("key-hash", keyHash, logTime);
        getPlugin().getConfiguration().LogAuthKeys("group", group, logTime, true);
    }

    public void GenerateKey(String group, String logName) {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Redeem command section
        if (label.equalsIgnoreCase("redeem")) {
            if (!(sender instanceof Player))
                return false;
            Player player = (Player)sender;
            if (args.length != 1) {
                getPlugin().getConfiguration().SendMessage("wrong-command-format", player);
                return true;
            }
            RedeemKey(player, args[0]);

            return true;
        }

        if (!label.equalsIgnoreCase("innocraft-authkey"))
            return false;

        //innocraft-authkey command section
        if (args.length == 0) {
            getPlugin().getConfiguration().SendMessage("wrong-command-format", sender);
            return true;
        }

        switch (args[0]) {
            case "add":
                if (args.length != 1 + 2 || args[1].length() <= 3) { //Requires 2 args and key length should be at least 4 chars
                    getPlugin().getConfiguration().SendMessage("wrong-command-format", sender);
                    return true;
                }

                if (!sender.hasPermission("innocraft.organizer")) {
                    getPlugin().getConfiguration().SendMessage("permission-error", sender);
                    return true;
                }

                AddKey(args[1], args[2], sender.getName());
                getPlugin().getConfiguration().SendMessage("auth-key-added", sender);

                return true;
            case "reload":

                if (!sender.hasPermission("innocraft.staff")) {
                    getPlugin().getConfiguration().SendMessage("permission-error", sender);
                    return true;
                }

                getPlugin().getConfiguration().ReloadAuthKeys();
                getPlugin().getConfiguration().SendMessage("auth-key-reloaded", sender);
                return true;
        }

        return true;
    }

}
