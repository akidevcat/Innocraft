package live.innocraft.essentials.auth;

import java.util.UUID;

public class AuthPlayer {

    private final UUID uniqueID;
    private String discordID;
    private String keyHash;
    private String permGroup;
    private String studyGroup;
    private String partyGroup;
    private String meta;
    private boolean isRegistered;
    private boolean isLoggedIn;
    private String registrationCode;
    private String verificationMessageID;

    public AuthPlayer(UUID uuid) {
        this.uniqueID = uuid;
        this.discordID = null;
        this.keyHash = null;
        this.permGroup = null;
        this.studyGroup = null;
        this.partyGroup = null;
        this.meta = null;
        this.verificationMessageID = null;
        isRegistered = false;
        isLoggedIn = false;
        registrationCode = null;
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public String getDiscordID() {
        return discordID;
    }

    public void setDiscordID(String discordID) {
        this.discordID = discordID;
    }

    public String getKeyHash() {
        return keyHash;
    }

    public void setKeyHash(String keyHash) {
        this.keyHash = keyHash;
    }

    public String getPermGroup() {
        return permGroup;
    }

    public void setPermGroup(String permGroup) {
        this.permGroup = permGroup;
    }

    public String getStudyGroup() {
        return studyGroup;
    }

    public void setStudyGroup(String studyGroup) {
        this.studyGroup = studyGroup;
    }

    public String getPartyGroup() {
        return partyGroup;
    }

    public void setPartyGroup(String partyGroup) {
        this.partyGroup = partyGroup;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getRegistrationCode() {
        return registrationCode;
    }

    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;
    }

    public String getVerificationMessageID() {
        return verificationMessageID;
    }

    public void setVerificationMessageID(String verificationMessageID) {
        this.verificationMessageID = verificationMessageID;
    }
}
