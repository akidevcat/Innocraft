package live.innocraft.aozora;

import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AozoraDiscordEvents extends ListenerAdapter {

    private AozoraDiscord aozoraDiscord;

    public AozoraDiscordEvents(AozoraDiscord aozoraDiscord) {
        this.aozoraDiscord = aozoraDiscord;
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event)
    {
        boolean result = aozoraDiscord.getModule(AozoraManager.class).authorizeUser(event.getMessageId(), event.getUserId());
        if (result) {
            event.getChannel().retrieveMessageById(event.getMessageId()).queue((message) -> {
                message.delete().queue();
            });
        }
    }

}
