package live.innocraft.hikari.Discord;

import live.innocraft.hikari.PluginCore.HikariPlugin;
import live.innocraft.hikari.PluginCore.HikariPluginModule;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

public class HikariDiscord extends HikariPluginModule {

    private JDA jda;
    private HikariDiscordConfiguration cfg;

    private Guild guild = null;

    public HikariDiscord(HikariPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onLateInitialization() {
        cfg = getPlugin().getConfiguration(HikariDiscordConfiguration.class);

        String token = cfg.getBotToken();

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
            e.printStackTrace();
        }

        this.jda = jda;
        this.guild = jda.getGuildById(cfg.getMainGuildID());

    }

    @Override
    public void onReload() {
        cfg.loadFile();
        guild = jda.getGuildById(cfg.getMainGuildID());
    }

    public void addEventListeners(Object... listeners) {
        jda.addEventListener(listeners);
    }

    public JDA getJDA() {
        return jda;
    }

}
