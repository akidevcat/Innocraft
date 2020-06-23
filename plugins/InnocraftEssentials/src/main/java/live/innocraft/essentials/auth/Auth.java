package live.innocraft.essentials.auth;

import live.innocraft.essentials.authkeys.DBAuthKey;
import live.innocraft.essentials.core.Essentials;
import live.innocraft.essentials.core.EssentialsModule;
import live.innocraft.essentials.discord.Discord;
import live.innocraft.essentials.sql.EssentialsSQL;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import static org.bukkit.Bukkit.getPlayer;
import static org.bukkit.Bukkit.getServer;

public class Auth extends EssentialsModule {

    private final HashMap<UUID, AuthPlayer> authPlayers;
    private final HashMap<String, UUID> registrationCodesCache;
    private final HashMap<String, AuthMessage> verificationMessages;
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

            generateAuthenticationMessage(authPlayer);

        }

        authPlayers.put(uuid, authPlayer);
        return authPlayer;
    }

    public void removeAuthPlayer(UUID uuid) {
        if (authPlayers.containsKey(uuid)) {
            String regCode = authPlayers.get(uuid).getRegistrationCode();
            if (regCode != null)
                registrationCodesCache.remove(regCode);
            authPlayers.remove(uuid);
        }
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

    public AuthMessage generateAuthenticationMessage(AuthPlayer authPlayer) {
        AuthMessage authMessage = new AuthMessage(authPlayer.getUniqueID(), authPlayer.getDiscordID());
        getPlugin().getModule(Discord.class).sendAuthenticationMessage(authMessage);
        return authMessage;
    }

    public void finalizeAuthenticationMessage(AuthMessage authMessage, String messageID) {
        authMessage.setMessageID(messageID);
        verificationMessages.put(messageID, authMessage);
    }

    public void authorizeUser(String messageID) {
        AuthMessage authMessage = verificationMessages.get(messageID);

        if (authMessage == null || authMessage.getMessageID() == null)
            return;

        AuthPlayer authPlayer = authPlayers.get(authMessage.getUniqueID());

        if (authPlayer == null)
            return;

        authPlayer.setLoggedIn(true);
        getPlugin().sendChatMessage("logged-in", getPlayer(authPlayer.getUniqueID()));
    }

    public void registerAuthPlayer(UUID uniqueID, String discordID) {
        EssentialsSQL sql = getPlugin().getModule(EssentialsSQL.class);
        sql.addAuthPlayer(uniqueID, discordID);
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
