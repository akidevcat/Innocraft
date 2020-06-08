package live.innocraft.essentials.discord;

import live.innocraft.essentials.Essentials;
import live.innocraft.essentials.classrooms.Classrooms;
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
            case "set-link":
                if (!event.getChannel().getId().equals(discord.channelCoreCommandsID))
                    return;
                if (!discord.getPlugin().getServerType().equals("general"))
                    return;
                if (msgArgs.length != 4 && msgArgs.length != 5)
                    return;

                Classrooms classrooms = discord.getPlugin().getModule(Classrooms.class);

                if (!classrooms.SetClassroomLink(msgArgs[2], msgArgs[3])) {
                    event.getChannel().sendMessage("This classroom doesn't exist [" + discord.getPlugin().getServer().getName() + "]").queue();
                    return;
                }
                if (msgArgs.length == 5) {
                    classrooms.SetClassroomCode(msgArgs[2], msgArgs[4]);
                    event.getChannel().sendMessage("Code was changed successfully [" + discord.getPlugin().getServer().getName() + "]").queue();
                } else {
                    classrooms.SetClassroomCode(msgArgs[2], "");
                }

                discord.SendLinkChanged(msgArgs[2]);

                event.getChannel().sendMessage("Link was changed successfully! [" + discord.getPlugin().getServer().getName() + "]").queue();
                break;
            case "help":
                discord.SendHelp();
                break;
        }
    }

}
