package live.innocraft.essentials.auth;

import live.innocraft.essentials.authkeys.DBAuthKey;
import live.innocraft.essentials.core.Essentials;
import live.innocraft.essentials.core.EssentialsModule;
import live.innocraft.essentials.discord.Discord;
import live.innocraft.essentials.sql.EssentialsSQL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import static org.bukkit.Bukkit.getPlayer;
import static org.bukkit.Bukkit.getServer;

public class Auth extends EssentialsModule {

    private final Long CONST_VERIFICATION_TIMEOUT = 20L * 60L * 1L;

    private final HashMap<UUID, AuthPlayer> authPlayers;
    private final HashMap<String, UUID> registrationCodesCache;
    private final HashMap<String, VerificationMessage> verificationMessages;
    private final Random random;

    public Auth(Essentials plugin) {
        super(plugin);

        authPlayers = new HashMap<>();
        registrationCodesCache = new HashMap<>();
        verificationMessages = new HashMap<>();
        random = new Random();

        getServer().getPluginManager().registerEvents(new AuthEvents(this), plugin);
    }

    @Override
    public void onReload() {

    }

    @Override
    public void onLateInitialization() {

    }

    public @Nullable AuthPlayer getAuthPlayer(UUID uuid) {
        return authPlayers.get(uuid);
    }

    public @Nullable UUID getRegCodeUUID(String code) {
        //return registrationCodesCache.get(code);
        return getPlugin().getModule(EssentialsSQL.class).getRegCodeUUID(code);
    }

    public void authorizePlayer(UUID uuid) {
        if (authPlayers.containsKey(uuid)) {
            authPlayers.get(uuid).setLoggedIn(true);
        }
    }

    public AuthPlayer addAuthPlayer(UUID uuid) {
        EssentialsSQL sql = getPlugin().getModule(EssentialsSQL.class);

        if (authPlayers.containsKey(uuid))
            return authPlayers.get(uuid);

        AuthPlayer authPlayer = new AuthPlayer(uuid);
        DBAuthPlayer dbAuthPlayer = sql.getAuthPlayer(uuid);
        if (dbAuthPlayer == null) {

            // Registration Phase
            authPlayer.setRegistrationCode(generateRegistrationCode(uuid));


        } else {

            authPlayer.setRegistered(true);
            authPlayer.setDiscordID(dbAuthPlayer.getDiscordID());
            authPlayer.setKeyHash(dbAuthPlayer.getKeyHash());

            // Fetch data
            if (dbAuthPlayer.getKeyHash() != null) {
                DBAuthKey dbAuthKey = sql.getAuthKey(dbAuthPlayer.getKeyHash());
                if (dbAuthKey != null) {
                    authPlayer.setPermGroup(dbAuthKey.getPermGroup());
                    authPlayer.setStudyGroup(dbAuthKey.getStudyGroup());
                    authPlayer.setPartyGroup(dbAuthKey.getPartyGroup());
                    authPlayer.setMeta(dbAuthKey.getMetaRaw());
                }
            }

            generateVerificationMessage(authPlayer);

        }

        authPlayers.put(uuid, authPlayer);
        return authPlayer;
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

    public boolean unregisterUser(String discordID) {
        UUID uuid = getPlugin().getModule(EssentialsSQL.class).deleteUser(discordID);
        if (uuid != null) {
            Bukkit.getPlayer(uuid).kickPlayer(getPlugin().getMessageColor("unregister-kick", "auth", "en_EN"));
            return true;
        }
        return false;
    }

    public String generateRegistrationCode(UUID uniqueID) {
        String code;
        do {
            code = random.ints(65, 90 + 1)
                    .limit(4)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

        } while (registrationCodesCache.containsKey(code));
        getPlugin().getModule(EssentialsSQL.class).addRegCode(code, uniqueID);
        registrationCodesCache.put(code, uniqueID);
        return code;
    }

    public VerificationMessage generateVerificationMessage(AuthPlayer authPlayer) {
        VerificationMessage verificationMessage = new VerificationMessage(authPlayer.getUniqueID(), authPlayer.getDiscordID());
        getPlugin().getModule(Discord.class).sendAuthenticationMessage(verificationMessage);
        return verificationMessage;
    }

    public void finalizeAuthenticationMessage(VerificationMessage verificationMessage, String messageID) {
        verificationMessage.setMessageID(messageID);
        verificationMessages.put(messageID, verificationMessage);
        authPlayers.get(verificationMessage.getUniqueID()).setVerificationMessageID(messageID);
        // Remove message after verification timeout
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            VerificationMessage updatedVerificationMessage = verificationMessages.get(messageID);
            if (updatedVerificationMessage != null && updatedVerificationMessage.getDate().equals(verificationMessage.getDate())) {
                verificationMessages.remove(messageID);
                Player p = getPlayer(verificationMessage.getUniqueID());
                if (p != null && !authPlayers.get(verificationMessage.getUniqueID()).isLoggedIn())
                    p.kickPlayer(getPlugin().getMessageColor("login-kick", "auth", p.getLocale()));
            }
        }, CONST_VERIFICATION_TIMEOUT);
    }

    public boolean authorizeUser(String messageID, String discordID) {
        VerificationMessage verificationMessage = verificationMessages.get(messageID);

        if (verificationMessage == null || verificationMessage.getMessageID() == null)
            return false;

        if (!verificationMessage.getDiscordID().equals(discordID))
            return false;

        AuthPlayer authPlayer = authPlayers.get(verificationMessage.getUniqueID());

        if (authPlayer == null)
            return false;

        authPlayer.setLoggedIn(true);
        getPlugin().sendChatMessage("logged-in", getPlayer(authPlayer.getUniqueID()));
        return true;
    }

    public void registerAuthPlayer(UUID uniqueID, String discordID) {
        EssentialsSQL sql = getPlugin().getModule(EssentialsSQL.class);
        sql.addAuthPlayer(uniqueID, discordID);
    }

    public boolean registerUser(String discordID, String code) {
        EssentialsSQL sql = getPlugin().getModule(EssentialsSQL.class);
        UUID uuid = sql.getRegCodeUUID(code);
        if (uuid == null) { // Incorrect code

            return false;
        }
        if (sql.getAuthPlayerByDiscord(discordID) != null) { // Player is already registered

            return false;
        }
        sql.deleteRegCode(code);
        registerAuthPlayer(uuid, discordID);
        return true; // Success
    }

//    public void registerPlayer(UUID uniqueID, String discordID) {
//        getPlugin().getModule(EssentialsSQL.class).addAuthPlayer(uniqueID, discordID);
//        cachePlayerDiscord(uniqueID, discordID);
//    }
//
//    public void registerPlayer(Player player, String discordID) {
//        registerPlayer(player.getUniqueId(), discordID);
//    }
//
//    public void cachePlayerDiscord(Player player, String discordID) {
//        cachePlayerDiscord(player.getUniqueId(), discordID);
//    }
//
//    public void cachePlayerDiscord(UUID uniqueID, String discordID) {
//        discordCache.put(uniqueID, discordID);
//    }
//
//    public void deleteCachePlayerDiscord(Player player) {
//        deleteCachePlayerDiscord(player.getUniqueId());
//    }
//
//    public void deleteCachePlayerDiscord(UUID uniqueID) {
//        discordCache.remove(uniqueID);
//    }
//
//    public String getDiscordID(Player player) {
//        return getDiscordID(player.getUniqueId());
//    }
//
//    public String getDiscordID(UUID uniqueID) {
//        return discordCache.get(uniqueID);
//    }
}
