package live.innocraft.aozora;

import live.innocraft.aozora.Structures.VerificationMessage;
import live.innocraft.hikari.Discord.HikariDiscord;
import live.innocraft.hikari.HikariCore;
import live.innocraft.hikari.PluginCore.HikariPlugin;
import live.innocraft.hikari.PluginCore.HikariPluginModule;

public class AozoraDiscord extends HikariPluginModule {

    private final HikariCore hikariCore;
    private final HikariDiscord hikariDiscord;

    public AozoraDiscord(HikariPlugin plugin) {
        super(plugin);

        hikariCore = HikariCore.getInstance();
        hikariDiscord = hikariCore.getDiscordModule();

        hikariDiscord.addEventListeners(new AozoraDiscordMessages(this));
        hikariDiscord.addEventListeners(new AozoraDiscordEvents(this));
    }

    public void sendAuthenticationMessage(VerificationMessage verificationMessage) {
        hikariDiscord.getJDA().retrieveUserById(verificationMessage.getDiscordID()).queue((user) -> {
            user.openPrivateChannel().queue((channel) -> {
                String textMsg = hikariCore.getMessageColor("discord-login-message", "auth", "en_EN");

                channel.sendMessage(textMsg).queue((msg) -> {
                    msg.addReaction(hikariCore.getMessageColor("discord-proceed-emoji", "auth", "en_EN")).queue();
                    getModule(AozoraManager.class).finalizeAuthenticationMessage(verificationMessage, msg.getId());
                });
            });
        });
    }

}
