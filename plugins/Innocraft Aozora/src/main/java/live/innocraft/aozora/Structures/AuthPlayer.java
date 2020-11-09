package live.innocraft.aozora.Structures;

import java.util.UUID;

public class AuthPlayer {

    private final UUID uniqueID;
    private String discordID;
    private boolean isRegistered;
    private boolean isLoggedIn;
    private String registrationCode;
    private String verificationMessageID;

    public AuthPlayer(UUID uuid) {
        this.uniqueID = uuid;
        this.discordID = null;
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
