package live.innocraft.hikari;

import live.innocraft.hikari.Common.*;
import live.innocraft.hikari.Discord.*;
import live.innocraft.hikari.PluginCore.*;
import live.innocraft.hikari.SQL.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;

public final class HikariCore extends HikariPlugin {

    /* Returns the instance of core plugin */
    public static HikariCore getInstance() {
        if (Bukkit.getPluginManager().isPluginEnabled("IC-Hikari"))
            return (HikariCore) Bukkit.getPluginManager().getPlugin("IC-Hikari");
        else
            return null;
    }

    public HikariSQL getSQLModule() {
        return getModule(HikariSQL.class);
    }

    public HikariDiscord getDiscordModule() {
        return getModule(HikariDiscord.class);
    }

    public HikariMessageConfiguration getMessageConfiguration() {
        return getConfiguration(HikariMessageConfiguration.class);
    }

    public HikariCoreConfiguration getCoreConfiguration() {
        return getConfiguration(HikariCoreConfiguration.class);
    }

    public HikariPlayerManager getPlayerManager() {
        return getModule(HikariPlayerManager.class);
    }

    public void kickPlayerSync(Player p, String msg) {
        Bukkit.getScheduler().runTask(this, () -> p.kickPlayer(msg));
    }

    @Override
    public ArrayList<Class<? extends HikariPluginModule>> getPluginModulesList() {
        ArrayList<Class<? extends HikariPluginModule>> modules = new ArrayList<>();
        modules.add(HikariPlayerManager.class);
        modules.add(HikariDiscord.class);
        modules.add(HikariSQL.class);
        return modules;
    }

    @Override
    public ArrayList<Class<? extends HikariPluginConfiguration>> getPluginConfigurationsList() {
        ArrayList<Class<? extends HikariPluginConfiguration>> cfgs = new ArrayList<>();
        cfgs.add(HikariDiscordConfiguration.class);
        cfgs.add(HikariCoreConfiguration.class);
        cfgs.add(HikariMessageConfiguration.class);
        return cfgs;
    }

    public String getServerType() {
        return getConfiguration(HikariCoreConfiguration.class).getServerType();
    }

    public void sendChatMessage(String msgLabel, CommandSender s) {
        if (s instanceof Player)
            sendChatMessage(msgLabel, (Player)s, ((Player)s).getLocale());
        else
            sendChatMessage(msgLabel, s, "en_EN");
    }

    public void sendChatMessage(String msgLabel, CommandSender s, String lang) {
        s.sendMessage(getChatMessageColor(msgLabel, lang));
    }

    public void sendChatMessageFormat(String msgLabel, CommandSender s, String... args) {
        if (s instanceof Player)
            sendChatMessageFormatLang(msgLabel, (Player)s, ((Player)s).getLocale(), args);
        else
            sendChatMessageFormatLang(msgLabel, s, "en_EN", args);
    }

    public void sendChatMessageFormatLang(String msgLabel, CommandSender s, String lang, String... args) {
        s.sendMessage(getChatMessageFormatColor(msgLabel, lang, args));
    }

    public String getChatMessageColor(String msgLabel, String lang) {
        return getConfiguration(HikariMessageConfiguration.class).getChatMessageColor(msgLabel, lang);
    }

    public String getChatMessageFormatColor(String msgLabel, String lang, String... args) {
        return getConfiguration(HikariMessageConfiguration.class).getChatMessageFormatColor(msgLabel, lang, args);
    }

    public String getMessageColor(String msgLabel, String subcategory, String lang) {
        return getConfiguration(HikariMessageConfiguration.class).getMessageColor(msgLabel, subcategory, lang);
    }

    public String getMessageColorFormat(String msgLabel, String subcategory, String lang, String... args) {
        return getConfiguration(HikariMessageConfiguration.class).getMessageColorFormat(msgLabel, subcategory, lang, args);
    }


}
