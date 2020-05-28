package live.innocraft.essentials.discord;

import live.innocraft.essentials.Essentials;
import live.innocraft.essentials.timetable.Timetable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordMessage extends ListenerAdapter {

    private Discord discord;

    public DiscordMessage(Discord discord) {
        this.discord = discord;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        Message msg = event.getMessage();
        String msgRaw = msg.getContentRaw();
        String[] msgArgs = msgRaw.split(" ");
        if (!msgArgs[0].equals("/ic"))
            return;
        if (msgArgs.length < 2)
            return;
        switch(msgArgs[1]) {
            case "sync":
                if (!event.getChannel().getId().equals(discord.channelCoreCommandsID))
                    return;
                discord.getPlugin().SyncAll();
                event.getChannel().sendMessage("Successfully synced all modules [" + discord.getPlugin().getServer().getName() + "]").queue();
                break;
            case "timetable":
                if (!event.getChannel().getId().equals(discord.channelCoreCommandsID))
                    return;
                if (!discord.getPlugin().getServerType().equals("general"))
                    return;
                discord.getPlugin().getModule(Discord.class).SendTimetable(discord.getPlugin().getModule(Timetable.class).getLessons());
                event.getChannel().sendMessage("Successfully sent a message [" + discord.getPlugin().getServer().getName() + "]").queue();
                break;
            case "clear-classes":
                if (!event.getChannel().getId().equals(discord.channelCoreCommandsID))
                    return;
                if (!discord.getPlugin().getServerType().equals("general"))
                    return;
                discord.ClearTextChannel(discord.channelClassesID);
                event.getChannel().sendMessage("Starting cleaning... [" + discord.getPlugin().getServer().getName() + "]").queue();
                break;
        }
    }

}
