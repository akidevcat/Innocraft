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
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.configuration.Configuration;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.logging.Level;

public class Discord extends EssentialsModule {

    private final JDA jda;

    private String roleParticipant = "";
    private String webhookClassesAvatarURL = "";

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
        roleParticipant = cfg.getString("discord.roles.participant");
        if (cfg.contains("discord.webhooks.classes.url"))
            webhookClassesAvatarURL = cfg.getString("discord.webhooks.classes.url");
    }

    @Override
    public void OnDisable() {
        jda.shutdown();
    }

    // Returns description hash
    public int SendClassesWebhook(TimetableLesson lesson) {
        Classrooms classrooms = getPlugin().getModule(Classrooms.class);

        if (!getPlugin().GetConfiguration().GetCfgCommon().contains("discord.webhooks.classes"))
            return -1;
        String url = getPlugin().GetConfiguration().GetCfgCommon().getString("discord.webhooks.classes.url");
        String username = getPlugin().GetConfiguration().GetCfgCommon().getString("discord.webhooks.classes.username");
        WebhookClient client = WebhookClient.withUrl(url);

        String description = "";

        description += "Начало: " + "**" + EssentialsHelper.ConvertMinutesToTimeString(lesson.TimeStart) + "**\n";
        description += "Окончание: " + "**" + EssentialsHelper.ConvertMinutesToTimeString(lesson.TimeEnd) + "**\n";
        if (!lesson.Classroom.equals(""))
            description += "Аудитория: " + "**" + classrooms.GetClassroomDisplayedName(lesson.Classroom) + "**\n"; // WHY IS IT NUL?????
        if (!lesson.Group.equals(""))
            description += "Группа: " + "**" + lesson.Group + "**\n";
        if (lesson.Compulsory)
            description += "\n**Обязательное мероприятие**\n";

        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setColor(0x4433DD)
                .setDescription(description)
                .setTitle(new WebhookEmbed.EmbedTitle(lesson.Name, null))
                .setAuthor(new WebhookEmbed.EmbedAuthor("Расписание", null, null))
                .build();

        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setUsername(username);
        if (!webhookClassesAvatarURL.equals(""))
            builder.setAvatarUrl(webhookClassesAvatarURL);
        builder.setContent("<@" + roleParticipant + ">");
        builder.addEmbeds(embed);
        client.send(builder.build());
        client.close();

        return description.hashCode();
    }


}
