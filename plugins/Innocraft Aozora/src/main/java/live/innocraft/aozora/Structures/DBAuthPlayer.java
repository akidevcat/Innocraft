package live.innocraft.aozora.Structures;

import live.innocraft.hikari.Helper.HikariHelper;

import java.util.UUID;

public class DBAuthPlayer {

    private final UUID uuid;
    private final String discord_id;


    public DBAuthPlayer(String uuid, String discord_id) {
        this.uuid = HikariHelper.parseDBUniqueID(uuid);
        this.discord_id = HikariHelper.parseDBString(discord_id);
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getDiscordID() {
        return discord_id;
    }
}
