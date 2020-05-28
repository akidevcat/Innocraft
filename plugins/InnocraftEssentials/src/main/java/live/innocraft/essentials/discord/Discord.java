package live.innocraft.essentials.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import live.innocraft.essentials.Essentials;
import live.innocraft.essentials.EssentialsModule;
import live.innocraft.essentials.classrooms.Classrooms;
import live.innocraft.essentials.helper.EssentialsHelper;
import live.innocraft.essentials.timetable.TimetableLesson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.configuration.Configuration;

import javax.security.auth.login.LoginException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class Discord extends EssentialsModule {

    private final JDA jda;

    protected String roleParticipantID = "";
    protected Role roleParticipant = null;
    protected String webhookClassesAvatarURL = "";
    protected String channelCoreCommandsID = "";
    protected String channelClassesID = "";
    protected String messagesClassesAuthor = "";
    protected String messagesClassesIconURL = "";
    protected int messagesClassesColor = 16777216;


    public Discord(Essentials plugin) {
        super(plugin);

        Configuration cfg = getPlugin().GetConfiguration().GetCfgCommon();
        String token = cfg.getString("discord.token");

        JDABuilder builder = new JDABuilder(token);
        JDA jda = null;

        builder.setDisabledCacheFlags(EnumSet.of(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE));
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setCompression(Compression.NONE);
        builder.setActivity(Activity.playing("Minecraft"));
        builder.addEventListeners(new DiscordMessage(this));

        try {
            jda = builder.build();
            jda.awaitReady();
        } catch (LoginException | InterruptedException e) {
            getPlugin().CriticalError("Unable to enable Discord bot, please check the token.");
            e.printStackTrace();
        }

        this.jda = jda; // Haka compiler

        Reload();
    }

    @Override
    public void LateInitialization() {

    }

    @Override
    public void Reload() {
        Configuration cfg = getPlugin().GetConfiguration().GetCfgCommon();
        roleParticipantID = cfg.getString("discord.roles.participant");
        roleParticipant = jda.getRoleById(roleParticipantID);
        channelCoreCommandsID = cfg.getString("discord.channels.core");
        channelClassesID = cfg.getString("discord.channels.classes");
        if (cfg.contains("discord.webhooks.classes.url"))
            webhookClassesAvatarURL = cfg.getString("discord.webhooks.classes.url");
        messagesClassesAuthor = cfg.getString("discord.messages.classes.author");
        messagesClassesIconURL = cfg.getString("discord.messages.classes.icon");
        messagesClassesColor = Integer.parseInt(Objects.requireNonNull(cfg.getString("discord.messages.classes.color", "0xFFFFFF").replace("0x", "")), 16);
    }

    @Override
    public void OnDisable() {
        jda.shutdown();
    }

    public void SendLessonNotification(TimetableLesson lesson) {
        Classrooms classrooms = getPlugin().getModule(Classrooms.class);

        MessageEmbed embed = GetLessonEmbed(lesson);

        Objects.requireNonNull(jda.getTextChannelById(channelClassesID)).sendMessage(embed).queue();
    }

    private static boolean ClearTextChannel_IsInProgress = false;
    public void ClearTextChannel(String channelID) {
        if (ClearTextChannel_IsInProgress)
            return;

        TextChannel channel = jda.getTextChannelById(channelID);

        ClearTextChannel_IsInProgress = true;

        new Thread(() ->
        {
            while (ClearTextChannel_IsInProgress) {
                List<Message> messages = channel.getHistory().retrievePast(50).complete();

                if (messages.isEmpty()) {
                    ClearTextChannel_IsInProgress = false;
                    return;
                }

                channel.deleteMessages(messages).complete();
            }
        }).start();
    }

    public void SendTimetable(Iterable<TimetableLesson> lessons) {
        jda.getTextChannelById(channelClassesID).sendMessage(GetLessonsEmbed(lessons)).queue();
        /*
        for (TimetableLesson lesson : lessons) {
            MessageEmbed embed = GetLessonEmbed(lesson);
            Objects.requireNonNull(jda.getTextChannelById(channelClassesID)).sendMessage(embed).queue();
        }
         */
    }

    private MessageEmbed GetLessonEmbed(TimetableLesson lesson) {
        Classrooms classrooms = getPlugin().getModule(Classrooms.class);

        String description = "";

        description += "Начало: " + "**" + EssentialsHelper.ConvertMinutesToTimeString(lesson.TimeStart) + "**\n";
        description += "Окончание: " + "**" + EssentialsHelper.ConvertMinutesToTimeString(lesson.TimeEnd) + "**\n";
        if (!lesson.Classroom.equals(""))
            description += "Аудитория: " + "**" + classrooms.GetClassroomDisplayedName(lesson.Classroom) + "**\n";
        if (!lesson.Group.equals(""))
            description += "Группа: " + "**" + lesson.Group + "**\n";
        if (lesson.Compulsory)
            description += "\n**Обязательное мероприятие**\n";

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(lesson.Name);
        eb.setAuthor(messagesClassesAuthor);
        eb.setColor(messagesClassesColor);
        eb.setDescription(description);
        eb.setImage(messagesClassesIconURL);

        return eb.build();
    }

    private MessageEmbed GetLessonsEmbed(Iterable<TimetableLesson> lessons) {
        Classrooms classrooms = getPlugin().getModule(Classrooms.class);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(EssentialsHelper.GetDate());
        eb.setTitle(messagesClassesAuthor);
        eb.setColor(messagesClassesColor);
        //eb.setDescription(description);

        for (TimetableLesson lesson : lessons) {
            String description = "";

            description += "**" + lesson.Name + "**\n";
            description += "Начало: " + "**" + EssentialsHelper.ConvertMinutesToTimeString(lesson.TimeStart) + "**\n";
            description += "Окончание: " + "**" + EssentialsHelper.ConvertMinutesToTimeString(lesson.TimeEnd) + "**\n";
            if (!lesson.Classroom.equals(""))
                description += "Место: " + "**" + classrooms.GetClassroomDisplayedName(lesson.Classroom) + "**\n";
            if (!lesson.Group.equals(""))
                description += "Группа: " + "**" + lesson.Group + "**\n";
            if (lesson.Compulsory)
                description += "*Обязательное*\n\n";

            eb.addField("Мероприятие", description, true);
        }

        eb.setThumbnail(messagesClassesIconURL);
        eb.setDescription(roleParticipant.getAsMention());

        return eb.build();
    }
}
