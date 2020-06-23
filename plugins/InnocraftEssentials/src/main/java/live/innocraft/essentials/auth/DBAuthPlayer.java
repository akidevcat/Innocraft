package live.innocraft.essentials.auth;

import java.util.UUID;

public class DBAuthPlayer {

    private final UUID uuid;
    private final String discord_id;
    private final String key_hash;

    public DBAuthPlayer(UUID uuid, String discord_id, String key_hash) {
        this.uuid = uuid;
        this.discord_id = discord_id;
        this.key_hash = key_hash;
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

}
