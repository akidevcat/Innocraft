package live.innocraft.essentials.common;

import live.innocraft.essentials.core.EssentialsConfiguration;
import live.innocraft.essentials.core.Essentials;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.Objects;

public class MessagesConfiguration extends EssentialsConfiguration {

    public MessagesConfiguration(Essentials plugin) {
        super(plugin, "messages.yml", true);
    }

    public String getMessageColor(String msgLabel, String subcategory, String lang) {
        // Try loading lang
        if (getCfgFile().contains(lang + "." + subcategory + "." + msgLabel))
            return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getCfgFile().getString(lang + "." + subcategory + "." + msgLabel)));
            // Try loading english lang
        else if (getCfgFile().contains("en-EN" + "." + subcategory + "." + msgLabel))
            return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getCfgFile().getString("en-EN" + "." + subcategory + "." + msgLabel)));
            // Send a null message
        else
            return ChatColor.translateAlternateColorCodes('&', "&cNull Message :( Please contact to the staff");
    }

    public String getMessageColorFormat(String msgLabel, String subcategory, String lang, String... args) {
        // Try loading lang
        if (getCfgFile().contains(lang + "." + subcategory + "." + msgLabel))
            return ChatColor.translateAlternateColorCodes('&', new MessageFormat(Objects.requireNonNull(getCfgFile().getString(lang + "." + subcategory + "." + msgLabel))).format(args) );
            // Try loading english lang
        else if (getCfgFile().contains("en-EN" + "." + subcategory + "." + msgLabel))
            return ChatColor.translateAlternateColorCodes('&', new MessageFormat(Objects.requireNonNull(getCfgFile().getString("en-EN" + "." + subcategory + "." + msgLabel))).format(args) );
            // Send a null message
        else
            return ChatColor.translateAlternateColorCodes('&', "&cNull Message :( Please contact to the staff");
    }

    public String getChatMessageColor(String msgLabel, String lang) {
        // Try loading lang
        if (getCfgFile().contains(lang + ".chat-messages." + msgLabel))
            return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getCfgFile().getString(lang + ".chat-messages." + msgLabel)));
            // Try loading english lang
        else if (getCfgFile().contains("en-EN" + ".chat-messages." + msgLabel))
            return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getCfgFile().getString("en-EN" + ".chat-messages." + msgLabel)));
            // Send a null message
        else
            return ChatColor.translateAlternateColorCodes('&', "&cNull Message :( Please contact to the staff");
    }

    public String getChatMessageFormatColor(String msgLabel, String lang, String... args) {
        // Try loading lang
        if (getCfgFile().contains(lang + ".chat-messages." + msgLabel))
            return ChatColor.translateAlternateColorCodes('&', new MessageFormat(Objects.requireNonNull(getCfgFile().getString(lang + ".chat-messages." + msgLabel))).format(args) );
            // Try loading english lang
        else if (getCfgFile().contains("en-EN" + ".chat-messages." + msgLabel))
            return ChatColor.translateAlternateColorCodes('&', new MessageFormat(Objects.requireNonNull(getCfgFile().getString("en-EN" + ".chat-messages." + msgLabel))).format(args) );
            // Send a null message
        else
            return ChatColor.translateAlternateColorCodes('&', "&cNull Message :( Please contact to the staff");
    }

    public void sendChatMessage(String msgLabel, CommandSender s, String lang) {
        s.sendMessage(getChatMessageColor(msgLabel, lang));
    }

    public void sendChatMessageFormat(String msgLabel, CommandSender s, String lang, String... args) {
        s.sendMessage(getChatMessageFormatColor(msgLabel, lang, args));
    }

}
