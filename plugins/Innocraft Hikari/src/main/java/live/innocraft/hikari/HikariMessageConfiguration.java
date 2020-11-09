package live.innocraft.hikari;

import live.innocraft.hikari.PluginCore.HikariPlugin;
import live.innocraft.hikari.PluginCore.HikariPluginConfiguration;
import live.innocraft.hikari.PluginCore.HikariPluginModule;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;

public class HikariMessageConfiguration extends HikariPluginConfiguration {

    private String prefixMessage;

    public HikariMessageConfiguration(HikariPlugin plugin) {
        super(plugin, "messages.yml", true);
    }

    private String getFormattedStringArgs(String path, String... args) {
        return ChatColor.translateAlternateColorCodes('&', (new MessageFormat(getCfgFile().getString(path).replace("{prefix}", prefixMessage).replaceAll("'", "''"))).format(args));
    }

    private String getFormattedString(String path) {
        return ChatColor.translateAlternateColorCodes('&', getCfgFile().getString(path).replace("{prefix}", prefixMessage));
    }

    @Override
    public void onReload() {
        if (getCfgFile().contains("en_EN.chat-messages.prefix"))
            prefixMessage = getCfgFile().getString("en_EN.chat-messages.prefix");
    }

    public String getMessageColor(String msgLabel, String subcategory, String lang) {
        // Try loading lang
        if (getCfgFile().contains(lang + "." + subcategory + "." + msgLabel))
            return getFormattedString(lang + "." + subcategory + "." + msgLabel);
            // Try loading english lang
        else if (getCfgFile().contains("en_EN" + "." + subcategory + "." + msgLabel))
            return getFormattedString("en_EN" + "." + subcategory + "." + msgLabel);
            // Send a null message
        else
            return ChatColor.translateAlternateColorCodes('&', "&cNull Message :( Please contact to the staff. Additional info: " + lang + "." + subcategory + "." + msgLabel);
    }

    public String getMessageColorFormat(String msgLabel, String subcategory, String lang, String... args) {
        // Try loading lang
        if (getCfgFile().contains(lang + "." + subcategory + "." + msgLabel))
            return getFormattedStringArgs(lang + "." + subcategory + "." + msgLabel, args);
            // Try loading english lang
        else if (getCfgFile().contains("en_EN" + "." + subcategory + "." + msgLabel))
            return getFormattedStringArgs("en_EN" + "." + subcategory + "." + msgLabel, args);
            // Send a null message
        else
            return ChatColor.translateAlternateColorCodes('&', "&cNull Message :( Please contact to the staff. Additional info: " + lang + "." + subcategory + "." + msgLabel);
    }

    public String getChatMessageColor(String msgLabel, String lang) {
        // Try loading lang
        if (getCfgFile().contains(lang + ".chat-messages." + msgLabel))
            return getFormattedString(lang + ".chat-messages." + msgLabel);
            // Try loading english lang
        else if (getCfgFile().contains("en_EN" + ".chat-messages." + msgLabel))
            return getFormattedString("en_EN" + ".chat-messages." + msgLabel);
            // Send a null message
        else
            return ChatColor.translateAlternateColorCodes('&', "&cNull Message :( Please contact to the staff. Additional info: " + lang + ".chat-messages." + msgLabel);
    }

    public String getChatMessageFormatColor(String msgLabel, String lang, String... args) {
        // Try loading lang
        if (getCfgFile().contains(lang + ".chat-messages." + msgLabel))
            return getFormattedStringArgs(lang + ".chat-messages." + msgLabel, args);
            // Try loading english lang
        else if (getCfgFile().contains("en_EN" + ".chat-messages." + msgLabel))
            return getFormattedStringArgs("en_EN.chat-messages." + msgLabel, args);
            // Send a null message
        else
            return ChatColor.translateAlternateColorCodes('&', "&cNull Message :( Please contact to the staff. Additional info: " + lang + ".chat-messages." + msgLabel);
    }

    public void sendChatMessage(String msgLabel, CommandSender s, String lang) {
        s.sendMessage(getChatMessageColor(msgLabel, lang));
    }

    public void sendChatMessageFormat(String msgLabel, CommandSender s, String lang, String... args) {
        s.sendMessage(getChatMessageFormatColor(msgLabel, lang, args));
    }

}
