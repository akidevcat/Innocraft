package live.innocraft.essentials.common;

import live.innocraft.essentials.core.EssentialsConfiguration;
import live.innocraft.essentials.core.Essentials;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.Objects;

public class MessagesConfiguration extends EssentialsConfiguration {

    private String prefixMessage;

    public MessagesConfiguration(Essentials plugin) {
        super(plugin, "messages.yml", true);
    }

    @Override
    public void onReload() {
        if (getCfgFile().contains("en_EN.chat-messages.prefix"))
            prefixMessage = getCfgFile().getString("en_EN.chat-messages.prefix");
    }

    public String getMessageColor(String msgLabel, String subcategory, String lang) {
        // Try loading lang
        if (getCfgFile().contains(lang + "." + subcategory + "." + msgLabel))
            return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getCfgFile().getString(lang + "." + subcategory + "." + msgLabel).replace("{prefix}", prefixMessage)));
            // Try loading english lang
        else if (getCfgFile().contains("en_EN" + "." + subcategory + "." + msgLabel))
            return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getCfgFile().getString("en_EN" + "." + subcategory + "." + msgLabel).replace("{prefix}", prefixMessage)));
            // Send a null message
        else
            return ChatColor.translateAlternateColorCodes('&', "&cNull Message :( Please contact to the staff");
    }

    public String getMessageColorFormat(String msgLabel, String subcategory, String lang, String... args) {
        // Try loading lang
        if (getCfgFile().contains(lang + "." + subcategory + "." + msgLabel))
            return ChatColor.translateAlternateColorCodes('&', new MessageFormat(Objects.requireNonNull(getCfgFile().getString(lang + "." + subcategory + "." + msgLabel).replace("{prefix}", prefixMessage))).format(args) );
            // Try loading english lang
        else if (getCfgFile().contains("en_EN" + "." + subcategory + "." + msgLabel))
            return ChatColor.translateAlternateColorCodes('&', new MessageFormat(Objects.requireNonNull(getCfgFile().getString("en_EN" + "." + subcategory + "." + msgLabel).replace("{prefix}", prefixMessage))).format(args) );
            // Send a null message
        else
            return ChatColor.translateAlternateColorCodes('&', "&cNull Message :( Please contact to the staff");
    }

    public String getChatMessageColor(String msgLabel, String lang) {
        // Try loading lang
        if (getCfgFile().contains(lang + ".chat-messages." + msgLabel))
            return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getCfgFile().getString(lang + ".chat-messages." + msgLabel).replace("{prefix}", prefixMessage)));
            // Try loading english lang
        else if (getCfgFile().contains("en_EN" + ".chat-messages." + msgLabel))
            return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getCfgFile().getString("en_EN" + ".chat-messages." + msgLabel).replace("{prefix}", prefixMessage)));
            // Send a null message
        else
            return ChatColor.translateAlternateColorCodes('&', "&cNull Message :( Please contact to the staff");
    }

    public String getChatMessageFormatColor(String msgLabel, String lang, String... args) {
        // Try loading lang
        if (getCfgFile().contains(lang + ".chat-messages." + msgLabel))
            return ChatColor.translateAlternateColorCodes('&', new MessageFormat(Objects.requireNonNull(getCfgFile().getString(lang + ".chat-messages." + msgLabel).replace("{prefix}", prefixMessage))).format(args) );
            // Try loading english lang
        else if (getCfgFile().contains("en_EN" + ".chat-messages." + msgLabel))
            return ChatColor.translateAlternateColorCodes('&', new MessageFormat(Objects.requireNonNull(getCfgFile().getString("en_EN" + ".chat-messages." + msgLabel).replace("{prefix}", prefixMessage))).format(args) );
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
