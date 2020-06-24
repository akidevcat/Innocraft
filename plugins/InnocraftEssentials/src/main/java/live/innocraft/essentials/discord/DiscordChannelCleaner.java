package live.innocraft.essentials.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nullable;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

public class DiscordChannelCleaner {

    public DiscordChannelCleaner(JDA jda, String channelID, @Nullable String containsText) {
        TextChannel channel = jda.getTextChannelById(channelID);

        new Thread(() ->
        {
            while (true) {
                List<Message> messages = channel.getHistory().retrievePast(50).complete();

                if (messages.isEmpty())
                    return;

                messages.forEach((Message m) -> {
                    if (containsText != null && m.getContentRaw().contains(containsText))
                        messages.remove(m);
                });

                channel.deleteMessages(messages).complete();
            }
        }).start();
    }

    public DiscordChannelCleaner(JDA jda, PrivateChannel channel, @Nullable String containsText) {
        new Thread(() ->
        {
            while (true) {
                List<Message> messages = channel.getHistory().retrievePast(50).complete();

                if (messages.isEmpty())
                    return;

                messages.forEach((Message m) -> {
                    if (m.getAuthor().getId().equals(jda.getSelfUser().getId()))
                        if (containsText == null || m.getContentRaw().contains(containsText))
                            if (Duration.between(m.getTimeCreated(), OffsetDateTime.now()).getSeconds() > 3)
                                m.delete().queue();
                            //channel.deleteMessageById(m.getId()).queue();
                });
                break;
            }
        }).start();
    }

}
