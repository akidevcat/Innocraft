package live.innocraft.essentials.discord;

import live.innocraft.essentials.core.EssentialsConfiguration;
import live.innocraft.essentials.core.Essentials;

import java.util.HashMap;

public class DiscordConfiguration extends EssentialsConfiguration {

    private String botToken;
    private String mainGuild;
    private final HashMap<String, String> roles;
    private final HashMap<String, String> channels;

    public DiscordConfiguration(Essentials plugin) {
        super(plugin, "discord.yml", true);

        roles = new HashMap<>();
        channels = new HashMap<>();
    }

    @Override
    public void onReload() {
        botToken = getCfgFile().getString("bot-token");
        mainGuild = getCfgFile().getString("main-guild");
        for (String role : getCfgFile().getStringList("roles")) {
            String[] parsed = role.split("@");
            roles.put(parsed[0], parsed[1]);
        }
        for (String channel : getCfgFile().getStringList("channels")) {
            String[] parsed = channel.split("@");
            channels.put(parsed[0], parsed[1]);
        }
    }

    public String getBotToken() {
        return botToken;
    }

    public String getMainGuildID() {
        return mainGuild;
    }

    public String getRoleID(String role) {
        return roles.get(role);
    }

    public String getChannelID(String channel) {
        return channels.get(channel);
    }
}
