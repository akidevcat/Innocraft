package live.innocraft.essentials.discord;

import live.innocraft.essentials.auth.Auth;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.EventListener;

public class DiscordEvents extends ListenerAdapter {

    private final Discord discord;

    public DiscordEvents(Discord discord) {
        this.discord = discord;
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event)
    {
        discord.getPlugin().getModule(Auth.class).authorizeUser(event.getMessageId());
    }

}
