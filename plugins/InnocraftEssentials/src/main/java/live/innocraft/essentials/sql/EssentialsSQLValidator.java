package live.innocraft.essentials.sql;

import live.innocraft.essentials.auth.DBAuthPlayer;
import live.innocraft.essentials.authkeys.DBAuthKey;
import live.innocraft.essentials.discord.Discord;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.Bukkit;

import java.util.Date;
import java.util.logging.Level;

public class EssentialsSQLValidator implements Runnable {

    private final EssentialsSQL sql;
    private final Discord discord;

    private int keyIterator = 0;

    private boolean isStopped = false;

    public EssentialsSQLValidator(EssentialsSQL sql) {
        this.sql = sql;
        this.discord = sql.getModule(Discord.class);

        Bukkit.getScheduler().runTaskTimerAsynchronously(sql.getPlugin(), this, 10L, 10L);
    }

    @Override
    public void run() {
        if (isStopped)
            return;

        validateExpiredKeys();
        validateParticipantRoleMembers();

    }

    private void validateExpiredKeys() {
        // Iterate through auth keys and remove expired keys
        DBAuthKey dbAuthKey = sql.getAuthKeyByID(keyIterator);

        if (dbAuthKey == null) {
            keyIterator = 0;
        } else {
            if (dbAuthKey.getUntil().before(new Date())) {
                DBAuthPlayer dbAuthPlayer = sql.getAuthPlayer(dbAuthKey.getUUID());
                if (dbAuthPlayer != null) {
                    discord.clearUserRoles(dbAuthPlayer.getDiscordID());
                    sql.deleteAuthKey(dbAuthPlayer.getKeyHash(), dbAuthPlayer.getUUID());
                } else {
                    sql.deleteAuthKey(dbAuthKey.getHash());
                }
            }

            keyIterator++;
        }
    }

    private void validateParticipantRoleMembers() {
        // Iterate through role members and remove ones without key

        String participantRoleID = discord.getRoleID("participant");

        for (Member member : discord.getRoleMembers(participantRoleID)) {
            DBAuthPlayer dbAuthPlayer = sql.getAuthPlayerByDiscord(member.getId());
            if (dbAuthPlayer == null)
                discord.removeUserRole(member.getId(), participantRoleID);
            else {
                DBAuthKey dbAuthKey = sql.getAuthKey(dbAuthPlayer.getKeyHash());
                if (dbAuthKey == null)
                    discord.removeUserRole(member.getId(), participantRoleID);
                else if (!dbAuthKey.getPermGroup().equals("participant"))
                    discord.removeUserRole(member.getId(), participantRoleID);
            }
        }
    }

    public void stop() {
        isStopped = true;
    }

}
