package live.innocraft.essentials.auth;

import java.util.Date;
import java.util.UUID;

public class AuthMessage {

    private String messageID;
    private Date date;
    private final UUID uuid;
    private final String discordID;

    public AuthMessage(UUID uuid, String discordID) {
        this.messageID = null;
        this.date = null;
        this.uuid = uuid;
        this.discordID = discordID;
    }

    public String getMessageID() {
        return messageID;
    }

    public Date getDate() {
        return date;
    }

    public UUID getUniqueID() {
        return uuid;
    }

    public String getDiscordID() {
        return discordID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
        this.date = new Date();
    }

}
