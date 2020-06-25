package live.innocraft.essentials.auth;

import live.innocraft.essentials.helper.EssentialsHelper;

import java.util.UUID;

public class DBAuthPlayer {

    private final UUID uuid;
    private final String discord_id;
    private final String key_hash;
    private final String lang;

    public DBAuthPlayer(String uuid, String discord_id, String key_hash, String lang) {
        this.uuid = EssentialsHelper.parseDBUniqueID(uuid);
        this.discord_id = EssentialsHelper.parseDBString(discord_id);
        this.key_hash = EssentialsHelper.parseDBString(key_hash);
        this.lang = EssentialsHelper.parseDBString(lang);
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getDiscordID() {
        return discord_id;
    }

    public String getKeyHash() {
        return key_hash;
    }

    public String getLang() {
        return lang;
    }
}
