package live.innocraft.essentials.discord;

import live.innocraft.essentials.auth.Auth;
import live.innocraft.essentials.auth.VerificationMessage;
import live.innocraft.essentials.core.Essentials;
import live.innocraft.essentials.core.EssentialsModule;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

public class Discord extends EssentialsModule {

    private JDA jda;
    private DiscordConfiguration cfg;

    private Guild guild = null;

//    protected String roleParticipantID = "";
//    protected Role roleParticipant = null;
//    protected String webhookClassesAvatarURL = "";
//    protected String channelCoreCommandsID = "";
//    protected String channelClassesID = "";
//    protected String messagesClassesAuthor = "";
//    protected String messagesClassesIconURL = "";
//    protected int messagesClassesColor = 16777216;
//    protected String messagesLinkDescription = "";
//    protected String messagesLinkIconURL = "";
//    protected int messagesLinkColor = 16777216;
//    protected Guild guild = null;

    public Discord(Essentials plugin) {
        super(plugin);
    }

    @Override
    public void onLateInitialization() {
        cfg = getPlugin().getConfiguration(DiscordConfiguration.class);

        String token = cfg.getBotToken();

        JDABuilder builder = new JDABuilder(token);
        JDA jda = null;

        builder.setDisabledCacheFlags(EnumSet.of(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE));
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setCompression(Compression.NONE);
        builder.setActivity(Activity.playing("Minecraft"));
        builder.addEventListeners(new DiscordMessage(this),
                                  new DiscordEvents(this));

        try {
            jda = builder.build();
            jda.awaitReady();
        } catch (LoginException | InterruptedException e) {
            getPlugin().criticalError("Unable to enable Discord bot, please check the token.");
            e.printStackTrace();
        }

        this.jda = jda; // Haka compiler
    }

    @Override
    public void onReload() {
        cfg.loadFile();
        guild = jda.getGuildById(cfg.getMainGuildID());
    }

    @Override
    public void onDisable() {
        jda.shutdown();
    }

//    public void SendLessonNotification(TimetableLesson lesson) {
//        Classrooms classrooms = getPlugin().getModule(Classrooms.class);
//
//        MessageEmbed embed = GetLessonEmbed(lesson);
//
//        Objects.requireNonNull(jda.getTextChannelById(channelClassesID)).sendMessage(embed).queue();
//    }

    public void sendAuthenticationMessage(VerificationMessage verificationMessage) {
        jda.getUserById(verificationMessage.getDiscordID()).openPrivateChannel().queue((channel) ->
        {
            String textMsg = getPlugin().getMessageColor("discord-login-message", "auth", "en_EN");

            // Delete all previous messages
            clearTextChannel(channel, textMsg);
            // Send a new one
            channel.sendMessage(textMsg).queue((msg) -> {
                msg.addReaction(getPlugin().getMessageColor("discord-proceed-emoji", "auth", "en_EN")).queue();
                getPlugin().getModule(Auth.class).finalizeAuthenticationMessage(verificationMessage, msg.getId());
            });
        });
    }

    public void clearTextChannel(String channelID) {
        new DiscordChannelCleaner(jda, channelID, null);
    }

    public void clearTextChannel(String channelID, String containsText) {
        new DiscordChannelCleaner(jda, channelID, containsText);
    }

    public void clearTextChannel(PrivateChannel channel, String containsText) {
        new DiscordChannelCleaner(jda, channel, containsText);
    }

//    private static boolean ClearTextChannel_IsInProgress = false;
//    public void ClearTextChannel(String channelID) {
//        if (ClearTextChannel_IsInProgress)
//            return;
//
//        TextChannel channel = jda.getTextChannelById(channelID);
//
//        ClearTextChannel_IsInProgress = true;
//
//        new Thread(() ->
//        {
//            while (ClearTextChannel_IsInProgress) {
//                List<Message> messages = channel.getHistory().retrievePast(50).complete();
//
//                if (messages.isEmpty()) {
//                    ClearTextChannel_IsInProgress = false;
//                    return;
//                }
//
//                channel.deleteMessages(messages).complete();
//            }
//        }).start();
//    }

    public void addUserRole(String discordID, String roleID) {
        Role role = jda.getRoleById(roleID);
        assert role != null;
        role.getGuild().addRoleToMember(discordID, role).queue();
    }

    public void removeUserRole(String discordID, String roleID) {
        Role role = jda.getRoleById(roleID);
        assert role != null;
        role.getGuild().removeRoleFromMember(discordID, role).queue();
    }

    public boolean isAdminChannel(String channelID) {
        return channelID.equals(getConfiguration(DiscordConfiguration.class).getChannelID("admin"));
    }

//    public void SendTimetable(Iterable<TimetableLesson> lessons) {
//        jda.getTextChannelById(channelClassesID).sendMessage(GetLessonsEmbed(lessons)).queue();
//        /*
//        for (TimetableLesson lesson : lessons) {
//            MessageEmbed embed = GetLessonEmbed(lesson);
//            Objects.requireNonNull(jda.getTextChannelById(channelClassesID)).sendMessage(embed).queue();
//        }
//         */
//    }

//    private MessageEmbed GetLessonEmbed(TimetableLesson lesson) {
//        Classrooms classrooms = getPlugin().getModule(Classrooms.class);
//
//        String description = "";
//
//        description += "Начало: " + "**" + EssentialsHelper.ConvertMinutesToTimeString(lesson.TimeStart) + "**\n";
//        description += "Окончание: " + "**" + EssentialsHelper.ConvertMinutesToTimeString(lesson.TimeEnd) + "**\n";
//        if (!lesson.Classroom.equals(""))
//            description += "Аудитория: " + "**" + classrooms.GetClassroomDisplayedName(lesson.Classroom) + "**\n";
//        if (!lesson.Group.equals(""))
//            description += "Группа: " + "**" + lesson.Group + "**\n";
//        if (lesson.Compulsory)
//            description += "\n**Обязательное мероприятие**\n";
//
//        EmbedBuilder eb = new EmbedBuilder();
//
//        eb.setTitle(lesson.Name);
//        eb.setAuthor(messagesClassesAuthor);
//        eb.setColor(messagesClassesColor);
//        eb.setDescription(description);
//        eb.setImage(messagesClassesIconURL);
//
//        return eb.build();
//    }

//    private MessageEmbed GetLessonsEmbed(Iterable<TimetableLesson> lessons) {
//        Classrooms classrooms = getPlugin().getModule(Classrooms.class);
//
//        EmbedBuilder eb = new EmbedBuilder();
//        eb.setAuthor(EssentialsHelper.GetDate());
//        eb.setTitle(messagesClassesAuthor);
//        eb.setColor(messagesClassesColor);
//        //eb.setDescription(description);
//
//        for (TimetableLesson lesson : lessons) {
//            String description = "";
//
//            description += "**" + lesson.Name + "**\n";
//            description += "Начало: " + "**" + EssentialsHelper.ConvertMinutesToTimeString(lesson.TimeStart) + "**\n";
//            description += "Окончание: " + "**" + EssentialsHelper.ConvertMinutesToTimeString(lesson.TimeEnd) + "**\n";
//            if (!lesson.Classroom.equals(""))
//                description += "Место: " + "**" + classrooms.GetClassroomDisplayedName(lesson.Classroom) + "**\n";
//            if (!lesson.Group.equals(""))
//                description += "Группа: " + "**" + lesson.Group + "**\n";
//            if (lesson.Compulsory)
//                description += "*Обязательное*\n\n";
//
//            eb.addField("Мероприятие", description, true);
//        }
//
//        eb.setThumbnail(messagesClassesIconURL);
//        eb.setDescription(roleParticipant.getAsMention());
//
//        return eb.build();
//    }

//    public void SendLinkChanged(String auditorium) {
//        jda.getTextChannelById(channelClassesID).sendMessage(GetLinkChangedEmbed(auditorium)).queue();
//    }

//    private MessageEmbed GetLinkChangedEmbed(String auditorium) {
//        Classrooms classrooms = getPlugin().getModule(Classrooms.class);
//
//        String code = classrooms.GetClassroomCode(auditorium);
//        String cname = classrooms.GetClassroomDisplayedName(auditorium);
//        String clink = classrooms.GetClassroomLink(auditorium);
//
//        EmbedBuilder eb = new EmbedBuilder();
//        eb.setAuthor(EssentialsHelper.GetDate());
//        eb.setTitle(cname, clink);
//        eb.setColor(messagesLinkColor);
//        eb.setDescription(roleParticipant.getAsMention() + "\n" + messagesLinkDescription + "\n" + (cname.equals("") ? "" : code));
//        eb.setThumbnail(messagesLinkIconURL);
//
//        return eb.build();
//    }

//    public void SendHelp() {
//        jda.getTextChannelById(channelCoreCommandsID).sendMessage(GetHelpEmbed()).queue();
//    }

//    private MessageEmbed GetHelpEmbed() {
//        EmbedBuilder eb = new EmbedBuilder();
//        eb.setAuthor(EssentialsHelper.GetDate());
//        eb.setTitle("Команды");
//        eb.setColor(messagesLinkColor);
//        eb.setDescription("/ic sync - Применяет изменения на сервер (для расписания)\n" +
//                "/ic timetable - Выводит текущее расписание в #classes\n" +
//                "/ic clear-classes - Очищает #classes\n" +
//                "/ic set-link <аудитория> <ссылка> [код] - Устанавливает ссылку и код для аудитории\n" +
//                "/ic help - Показывает данное сообщение\n");
//        eb.setThumbnail(messagesLinkIconURL);
//
//        return eb.build();
//    }

    public Member getMemberByID(String discordID) {
        return guild.getMemberById(discordID);
    }
}
