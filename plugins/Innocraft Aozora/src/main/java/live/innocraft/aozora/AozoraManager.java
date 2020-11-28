package live.innocraft.aozora;

import live.innocraft.aozora.Structures.AuthPlayer;
import live.innocraft.aozora.Structures.DBAuthPlayer;
import live.innocraft.aozora.Structures.VerificationMessage;
import live.innocraft.hikari.HikariCore;
import live.innocraft.hikari.HikariCoreConfiguration;
import live.innocraft.hikari.PluginCore.HikariPlugin;
import live.innocraft.hikari.PluginCore.HikariPluginModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static org.bukkit.Bukkit.getPlayer;

public class AozoraManager extends HikariPluginModule {

    private final HikariPlugin plugin;
    private final HikariCore hikariCore;

    private final HashMap<UUID, AuthPlayer> authPlayers;
    private final HashMap<String, UUID> registrationCodesCache;
    private final HashMap<String, VerificationMessage> verificationMessages;
    private final Random random;

    private final AozoraEvents aozoraEvents;

    public AozoraManager(HikariPlugin plugin) {
        super(plugin);

        this.plugin = plugin;
        hikariCore = HikariCore.getInstance();
        authPlayers = new HashMap<>();
        registrationCodesCache = new HashMap<>();
        verificationMessages = new HashMap<>();
        random = new Random();
        System.out.println("POINT 1");
        aozoraEvents = new AozoraEvents(this);
        plugin.getServer().getPluginManager().registerEvents(aozoraEvents, plugin);
    }

    /*
    This method is called on each player joining the server
     */
    AuthPlayer addAuthPlayer(UUID uuid) {
        AozoraSQL sql = getModule(AozoraSQL.class);

        if (authPlayers.containsKey(uuid))
            return authPlayers.get(uuid);

        AuthPlayer authPlayer = new AuthPlayer(uuid);
        DBAuthPlayer dbAuthPlayer = sql.getAuthPlayer(uuid);
        if (dbAuthPlayer == null) {

            // Registration Phase
            authPlayer.setRegistrationCode(generateNewRegistrationCode(uuid));


        } else {

            authPlayer.setRegistered(true);
            authPlayer.setDiscordID(dbAuthPlayer.getDiscordID());

            if (hikariCore.getConfiguration(HikariCoreConfiguration.class).getServerType().equals("auth"))
                generateVerificationMessage(authPlayer);
            else
                authPlayer.setLoggedIn(true);

        }

        authPlayers.put(uuid, authPlayer);
        return authPlayer;
    }

    public @Nullable AuthPlayer getAuthPlayer(UUID uuid) {
        return authPlayers.get(uuid);
    }

    public void removeAuthPlayer(UUID uuid) {
        AuthPlayer authPlayer = authPlayers.get(uuid);
        if (authPlayer != null) {
            String regCode = authPlayer.getRegistrationCode();
            if (regCode != null)
                registrationCodesCache.remove(regCode);
            authPlayers.remove(uuid);
            verificationMessages.remove(authPlayer.getVerificationMessageID());
        }
    }

    private String generateNewRegistrationCode(UUID uniqueID) {
        AozoraSQL sql = getModule(AozoraSQL.class);
        String code;
        do {
            code = random.ints(65, 90 + 1)
                    .limit(6)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

        } while (registrationCodesCache.containsKey(code));
        sql.addRegCode(code, uniqueID);
        registrationCodesCache.put(code, uniqueID);

        // Remove code after timeout
        final String finalCode = code;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            sql.deleteRegCode(finalCode);
        }, AozoraConstants.CONST_REGISTRATION_TIMEOUT);

        return code;
    }

    private VerificationMessage generateVerificationMessage(AuthPlayer authPlayer) {
        VerificationMessage verificationMessage = new VerificationMessage(authPlayer.getUniqueID(), authPlayer.getDiscordID());
        getModule(AozoraDiscord.class).sendAuthenticationMessage(verificationMessage);
        return verificationMessage;
    }

    void finalizeAuthenticationMessage(VerificationMessage verificationMessage, String messageID) {
        verificationMessage.setMessageID(messageID);
        verificationMessages.put(messageID, verificationMessage);
        if (authPlayers.containsKey(verificationMessage.getUniqueID()))
            authPlayers.get(verificationMessage.getUniqueID()).setVerificationMessageID(messageID);
        // Remove message after verification timeout
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            VerificationMessage updatedVerificationMessage = verificationMessages.get(messageID);
            if (updatedVerificationMessage != null && updatedVerificationMessage.getDate().equals(verificationMessage.getDate())) {
                verificationMessages.remove(messageID);
                Player p = getPlayer(verificationMessage.getUniqueID());
                if (p != null && !authPlayers.get(verificationMessage.getUniqueID()).isLoggedIn())
                    p.kickPlayer(hikariCore.getMessageColor("login-kick", "auth", p.getLocale()));
            }
        }, AozoraConstants.CONST_VERIFICATION_TIMEOUT);
    }

    boolean authorizeUser(String messageID, String discordID) {
        VerificationMessage verificationMessage = verificationMessages.get(messageID);

        if (verificationMessage == null || verificationMessage.getMessageID() == null)
            return false;

        if (!verificationMessage.getDiscordID().equals(discordID))
            return false;

        AuthPlayer authPlayer = authPlayers.get(verificationMessage.getUniqueID());

        if (authPlayer == null)
            return false;

        authPlayer.setLoggedIn(true);
        // Remove restricted player on Bungee side
        getPlugin().invokeProxyMethod("Auth", "removeBlockedPlayerString", authPlayer.getUniqueID().toString());
        // Move player to default server
        hikariCore.sendChatMessage("logged-in", getPlayer(authPlayer.getUniqueID()));

        aozoraEvents.onLogin(authPlayer);

        return true;
    }

    public byte registerUser(String discordID, String code) {
        AozoraSQL sql = getModule(AozoraSQL.class);
        UUID uuid = sql.getRegCodeUUID(code);
        if (uuid == null) { // Incorrect code
            return 1;
        }
        if (sql.getAuthPlayerByDiscord(discordID) != null) { // Player is already registered
            return 2;
        }
        sql.deleteRegCode(code);
        sql.addAuthPlayer(uuid, discordID);
        return 0; // Success
    }

    public boolean unregisterUser(String discordID) {
        UUID uuid = getModule(AozoraSQL.class).deleteUser(discordID);
        if (uuid != null) {
            Bukkit.getScheduler().runTask(getPlugin(), () -> { // Exit from async thread
                if (getPlayer(uuid) != null)
                    Objects.requireNonNull(getPlayer(uuid)).kickPlayer(hikariCore.getMessageColor("unregister-kick", "auth", "en_EN"));
            });
            return true;
        }
        return false;
    }

}
