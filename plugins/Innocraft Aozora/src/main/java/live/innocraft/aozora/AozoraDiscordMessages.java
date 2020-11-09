package live.innocraft.aozora;

import live.innocraft.hikari.HikariCore;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AozoraDiscordMessages extends ListenerAdapter {

    private AozoraDiscord aozoraDiscord;
    private HikariCore hikariCore;

    public AozoraDiscordMessages(AozoraDiscord aozoraDiscord) {
        this.aozoraDiscord = aozoraDiscord;
        this.hikariCore = HikariCore.getInstance();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        String msgRaw = msg.getContentRaw();
        String[] msgArgs = msgRaw.split(" ");
        byte responseCode = 0;
        switch (msgArgs[0]) {
            case "/register":
                if (!hikariCore.getServerType().equals("auth"))
                    return;
                if (!event.isFromType(ChannelType.PRIVATE))
                    return;
                if (msgArgs.length != 2)
                    return;
                if (msgArgs[1].length() != 6)
                    return;
                for (char c : msgArgs[1].toCharArray()) {
                    if (c < 65 || c > 90)
                        return;
                }
                event.getAuthor().openPrivateChannel().queue((channel) -> {
                    switch (aozoraDiscord.getModule(AozoraManager.class).registerUser(event.getAuthor().getId(), msgArgs[1])) {
                        case 0:
                            channel.sendMessage(hikariCore.getMessageColor("discord-register-message", "auth", "en_EN")).queue();
                            break;
                        case 1:
                            channel.sendMessage(hikariCore.getMessageColor("discord-register-wrong-code-message", "auth", "en_EN")).queue();
                            break;
                        case 2:
                            channel.sendMessage(hikariCore.getMessageColor("discord-register-already-done-message", "auth", "en_EN")).queue();
                            break;
                    }
                });
                return;
            case "/unregister":
                if (!hikariCore.getServerType().equals("auth"))
                    return;
                if (!event.isFromType(ChannelType.PRIVATE))
                    return;
                if (aozoraDiscord.getModule(AozoraManager.class).unregisterUser(event.getAuthor().getId()))
                    event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(
                            hikariCore.getMessageColor("discord-unregister-message", "auth", "en_EN")
                    ).queue());
                break;
            case "/ic":
                break;
            default:
                return;
        }
    }

}
