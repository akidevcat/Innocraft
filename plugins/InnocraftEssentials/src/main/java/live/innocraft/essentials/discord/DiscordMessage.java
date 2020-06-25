package live.innocraft.essentials.discord;

import live.innocraft.essentials.auth.Auth;
import live.innocraft.essentials.authkeys.AuthKeys;
import live.innocraft.essentials.classrooms.Classrooms;
import live.innocraft.essentials.sql.EssentialsSQL;
import live.innocraft.essentials.timetable.Timetable;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
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
        byte responseCode = 0;
        switch (msgArgs[0]) {
            case "/redeem":
                if (!event.isFromType(ChannelType.PRIVATE))
                    return;
                if (msgArgs.length != 2)
                    return;
                event.getAuthor().openPrivateChannel().queue((channel) -> {
                    switch (discord.getPlugin().getModule(AuthKeys.class).redeemUserAuthKey(event.getAuthor().getId(), msgArgs[1])) {
                        case 0:
                            channel.sendMessage(discord.getPlugin().getMessageColor("discord-redeem-message", "auth", "en_EN")).queue();
                            break;
                        case 1:
                            channel.sendMessage(discord.getPlugin().getMessageColor("discord-redeem-not-registered-message", "auth", "en_EN")).queue();
                            break;
                        case 2:
                            channel.sendMessage(discord.getPlugin().getMessageColor("discord-redeem-invalid-key-message", "auth", "en_EN")).queue();
                            break;
                        case 3:
                            channel.sendMessage(discord.getPlugin().getMessageColor("discord-redeem-already-active-message", "auth", "en_EN")).queue();
                            break;
                    }
                });
                return;
            case "/register":
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
                    switch (discord.getPlugin().getModule(Auth.class).registerUser(event.getAuthor().getId(), msgArgs[1])) {
                        case 0:
                            channel.sendMessage(discord.getPlugin().getMessageColor("discord-register-message", "auth", "en_EN")).queue();
                            break;
                        case 1:
                            channel.sendMessage(discord.getPlugin().getMessageColor("discord-register-wrong-code-message", "auth", "en_EN")).queue();
                            break;
                        case 2:
                            channel.sendMessage(discord.getPlugin().getMessageColor("discord-register-already-done-message", "auth", "en_EN")).queue();
                            break;
                    }
                });
                return;
            case "/unregister":
                if (!event.isFromType(ChannelType.PRIVATE))
                    return;
                if (discord.getPlugin().getModule(Auth.class).unregisterUser(event.getAuthor().getId()))
                    event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(
                            discord.getPlugin().getMessageColor("discord-unregister-message", "auth", "en_EN")
                    ).queue());
                break;
            case "/ic":
                break;
            default:
                return;
        }

        // /ic Command

        if (msgArgs.length < 2)
            return;

        switch(msgArgs[1]) {

        }
//            case "sync":
//                if (!event.getChannel().getId().equals(discord.channelCoreCommandsID))
//                    return;
//                discord.getPlugin().syncAll();
//                event.getChannel().sendMessage("Successfully synced all modules [" + discord.getPlugin().getServer().getName() + "]").queue();
//                break;
//            case "timetable":
//                if (!event.getChannel().getId().equals(discord.channelCoreCommandsID))
//                    return;
//                if (!discord.getPlugin().getServerType().equals("general"))
//                    return;
//                discord.getPlugin().getModule(Discord.class).SendTimetable(discord.getPlugin().getModule(Timetable.class).getLessons());
//                event.getChannel().sendMessage("Successfully sent a message [" + discord.getPlugin().getServer().getName() + "]").queue();
//                break;
//            case "clear-classes":
//                if (!event.getChannel().getId().equals(discord.channelCoreCommandsID))
//                    return;
//                if (!discord.getPlugin().getServerType().equals("general"))
//                    return;
//                discord.ClearTextChannel(discord.channelClassesID);
//                event.getChannel().sendMessage("Starting cleaning... [" + discord.getPlugin().getServer().getName() + "]").queue();
//                break;
//            case "set-link":
//                if (!event.getChannel().getId().equals(discord.channelCoreCommandsID))
//                    return;
//                if (!discord.getPlugin().getServerType().equals("general"))
//                    return;
//                if (msgArgs.length != 4 && msgArgs.length != 5)
//                    return;
//
//                Classrooms classrooms = discord.getPlugin().getModule(Classrooms.class);
//
//                if (!classrooms.SetClassroomLink(msgArgs[2], msgArgs[3])) {
//                    event.getChannel().sendMessage("This classroom doesn't exist [" + discord.getPlugin().getServer().getName() + "]").queue();
//                    return;
//                }
//                if (msgArgs.length == 5) {
//                    classrooms.SetClassroomCode(msgArgs[2], msgArgs[4]);
//                    event.getChannel().sendMessage("Code was changed successfully [" + discord.getPlugin().getServer().getName() + "]").queue();
//                } else {
//                    classrooms.SetClassroomCode(msgArgs[2], "");
//                }
//
//                discord.SendLinkChanged(msgArgs[2]);
//
//                event.getChannel().sendMessage("Link was changed successfully! [" + discord.getPlugin().getServer().getName() + "]").queue();
//                break;
//            case "help":
//                discord.SendHelp();
//                break;
//        }
    }

}
