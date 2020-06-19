package live.innocraft.essentials.common;

import live.innocraft.essentials.configuration.EssentialsConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.Objects;

public class MessagesConfiguration extends EssentialsConfiguration {

    public MessagesConfiguration(Essentials plugin) {
        super(plugin, "messages.yml", true);
    }

    public void sendMessage(String msgLabel, CommandSender s, String lang) {
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getCfgFile().getString(lang + ".messages." + msgLabel))));
    }

    public void sendMessageFormat(String msgLabel, CommandSender s, String lang, String... args) {
        MessageFormat format = new MessageFormat((Objects.requireNonNull(getCfgFile().getString(lang + ".messages." + msgLabel))));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', format.format(args)));
    }

}
