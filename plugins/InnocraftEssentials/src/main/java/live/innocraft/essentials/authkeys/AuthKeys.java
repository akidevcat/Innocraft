package live.innocraft.essentials.authkeys;

import live.innocraft.essentials.auth.AuthPlayer;
import live.innocraft.essentials.auth.DBAuthPlayer;
import live.innocraft.essentials.core.Essentials;
import live.innocraft.essentials.discord.Discord;
import live.innocraft.essentials.helper.EssentialsHelper;
import live.innocraft.essentials.core.EssentialsModule;
import live.innocraft.essentials.sql.EssentialsSQL;
import me.stefan911.securitymaster.lite.utils.account.AccountManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Set;

public class AuthKeys extends EssentialsModule implements CommandExecutor {

    public AuthKeys(Essentials plugin) {
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

    public short redeemUserAuthKey(String discordID, String key) {
        EssentialsSQL sql = getPlugin().getModule(EssentialsSQL.class);

        String keyHash = EssentialsHelper.HashSHA256(key);
        DBAuthPlayer dbAuthPlayer = sql.getAuthPlayerByDiscord(discordID);

        if (dbAuthPlayer == null) {
            return 1; // User is not registered
        }

        DBAuthKey dbAuthKey = sql.getAuthKey(keyHash);
        if (dbAuthKey == null) {
            return 2; // Invalid key
        }

        if (dbAuthKey.getUUID() != null) {
            return 3; // Key is already active
        }

        sql.resetAuthKey(discordID, keyHash);
        sql.setAuthPlayerAuthKey(dbAuthPlayer.getUUID(), keyHash);

        Player p = Bukkit.getPlayer(dbAuthPlayer.getUUID());
        if (p != null)
            getPlugin().kickPlayerSync(p, getPlugin().getMessageColor("authkey-kick", "auth", p.getLocale()));

        return 0; //Success
    }

    public void redeemPlayerAuthKey() {

    }

    public short syncOnlinePlayerAuthKey(Player player, String keyHash) {
        AuthKeysConfiguration cfg = getPlugin().getConfiguration(AuthKeysConfiguration.class);
        EssentialsSQL sql = getPlugin().getModule(EssentialsSQL.class);

        DBAuthKey aKey = sql.getAuthKey(keyHash);
        if (aKey == null)
            return 1; // Key is invalid

        if (aKey.getUntil().toInstant().isBefore(ZonedDateTime.now().toInstant())) {
            sql.deleteAuthKey(keyHash, player.getUniqueId());
            return 2; // Key expired
        }

        // Set Discord Roles

        // Set Permission Groups
        getPlugin().setPlayerPermissionGroup(player, );

        return 0; // Success
    }

//    public boolean RedeemKey (Player player, String key) {
//        AuthKeysConfiguration cfg = getPlugin().getConfiguration(AuthKeysConfiguration.class);
//        EssentialsSQL sql = getPlugin().getModule(EssentialsSQL.class);
//        String keyHash = EssentialsHelper.HashSHA256(key);
//
//        DBAuthKey aKey = sql.getAuthKey(keyHash);
//
//        if (aKey == null) {
//            getPlugin().sendChatMessage("auth-key-wrong", player);
//            return false;
//        }
//
//        if (aKey.getUntil().toInstant().isBefore(ZonedDateTime.now().toInstant())) {
//            sql.deleteAuthKey(keyHash, player.getUniqueId());
//            return false;
//        }
//
//        sql.setAuthPlayerAuthKey(player.getUniqueId(), keyHash);
//
//
//
//        FileConfiguration cfgAuthKeys = getPlugin().getConfiguration().GetCfgAuthKeys();
//        FileConfiguration cfgCommon = getPlugin().getConfiguration().GetCfgCommon();
//        String keyHash = EssentialsHelper.HashSHA256(key);
//        Set<String> cfgKeys = cfgAuthKeys.getKeys(false);
//        Set<String> groups = cfgCommon.getConfigurationSection("auth-keys.groups").getKeys(false);
//
//        if (!cfgKeys.contains(keyHash)) {
//            getPlugin().getConfiguration().SendMessage("auth-key-wrong", player);
//            return false;
//        }
//
//        String keyGroup = cfgAuthKeys.getString(keyHash);
//
//        // Log this action
//        String logTime = EssentialsHelper.GetTimeStamp();
//        getPlugin().getConfiguration().LogAuthKeys("info", "An auth key was redeemed", logTime);
//        getPlugin().getConfiguration().LogAuthKeys("who", player.getName(), logTime);
//        getPlugin().getConfiguration().LogAuthKeys("key-hash", keyHash, logTime, true);
//
//        //Check if the config contains this group
//        if (!groups.contains(keyGroup)) {
//            getPlugin().getConfiguration().SendMessage("auth-key-wrong-group", player);
//            return false;
//        }
//
//        //Clear the key from the config file and save it
//        cfgAuthKeys.set(keyHash, null);
//        getPlugin().getConfiguration().SaveAuthKeys();
//
//        //Execute all the group commands
//        for (String command : cfgCommon.getStringList("auth-keys.groups." + keyGroup + ".commands")) {
//            String parsedCommand = command.replaceAll("\\{player\\}", player.getName());
//            getPlugin().getServer().dispatchCommand(getPlugin().getServer().getConsoleSender(), parsedCommand);
//        }
//
//        if (cfgCommon.contains("auth-keys.groups." + keyGroup + ".role")) {
//            //SecurityMasterAPI smapi = getPlugin().getDependency(SecurityMasterAPI.class);
//            String discordID = (new AccountManager(player.getUniqueId())).getDiscordID();
//            //String discordID = getPlugin().getModule(Auth.class).getDiscordID(player);
//            String roleID = cfgCommon.getString("auth-keys.groups." + keyGroup + ".role");
//            if (discordID == null) {
//                getPlugin().getConfiguration().SendMessage("auth-key-discord-problem", player);
//                getPlugin().getConfiguration().LogAuthKeys("role", "Unable to set role - player is not registered", logTime, true);
//            }
//            else {
//                getPlugin().getModule(Discord.class).AddUserRole(discordID, roleID);
//            }
//        }
//
//        //Send a message to the player
//        getPlugin().getConfiguration().SendMessage("auth-key-redeemed", player);
//
//        return true;
//    }

    public void AddKey (String key, String group, String until, String logName) {
        if (key.length() <= 3)
            return;

        getPlugin().getModule(EssentialsSQL.class).addAuthKey(DBAuthKey.createEmpty(key).setPermGroup(group).setUntil(until));
        //AuthKeysConfiguration cfg = getPlugin().getConfiguration(AuthKeysConfiguration.class);

        //getPlugin().getModule(EssentialsSQL.class).

        //cfg.set(keyHash, group);
        //getPlugin().getConfiguration().SaveAuthKeys();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Redeem command section
        Player player = null;
        if (sender instanceof Player)
            player = (Player)sender;

        if (!label.equalsIgnoreCase("innocraft-authkey"))
            return false;

        //innocraft-authkey command section
        if (args.length == 0) {
            getPlugin().sendChatMessage("wrong-command-format", sender);
            return true;
        }

        switch (args[0]) {
            case "redeem":
                if (args.length != 1) {
                    getPlugin().sendChatMessage("wrong-command-format", sender);
                    return true;
                }
                //RedeemKey(player, args[0]);
                return true;
            case "create":
            case "add":
                if (args.length != 1 + 3 || args[1].length() <= 3) { //Requires 2 args and key length should be at least 4 chars

                    getPlugin().sendChatMessage("wrong-command-format", sender);
                    return true;
                }

                if (!sender.hasPermission("innocraft.organizer")) {
                    getPlugin().sendChatMessage("permission-error", sender);
                    return true;
                }

                AddKey(args[1], args[2], args[3], sender.getName());
                getPlugin().sendChatMessage("auth-key-added", sender);

                return true;
            case "reload":

                if (!sender.hasPermission("innocraft.staff")) {
                    getPlugin().sendChatMessage("permission-error", sender);
                    return true;
                }

                //getPlugin().getConfiguration().ReloadAuthKeys();
                getPlugin().sendChatMessage("auth-key-reloaded", sender);
                return true;
        }

        return true;
    }

}
