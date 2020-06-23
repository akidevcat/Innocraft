package live.innocraft.essentials.sql;

import live.innocraft.essentials.auth.DBAuthPlayer;
import live.innocraft.essentials.authkeys.DBAuthKey;
import live.innocraft.essentials.common.DBParticipant;
import live.innocraft.essentials.core.Essentials;
import live.innocraft.essentials.core.EssentialsModule;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.UUID;

public class EssentialsSQL extends EssentialsModule {

    private final String CONST_HOST = "localhost";
    private final int CONST_PORT = 3306;
    private final String CONST_DATABASE = "innocraft";
    private final String CONST_USERNAME = "innocraft";
    private final String CONST_PASSWORD = "innocraft";
    private final String CONST_TABLE_NAME_PLAYERS = "ic_players";
    private final String CONST_TABLE_NAME_REGCODES = "ic_regcodes";
    private final String CONST_TABLE_NAME_AUTHKEYS = "ic_authkeys";

    private Connection connection;

    public EssentialsSQL(Essentials plugin) {
        super(plugin);

        openConnection();
        setupTables();
    }

    // Gets an Authentication player from the database
    public @Nullable
    DBAuthPlayer getAuthPlayer(UUID uuid) {
        try {

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM" + CONST_TABLE_NAME_PLAYERS + "WHERE" + "UUID" + "= '" + uuid.toString() + "';");
            if (result.next())
                return new DBAuthPlayer(uuid, result.getString("DISCORD_ID"), result.getString("KEY_HASH"));

            return null;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            getPlugin().criticalError("Error occurred while getting SQL auth player");

            return null;
        }
    }

    public @Nullable
    DBAuthKey getAuthKey(String hash) {
        try {

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM" + CONST_TABLE_NAME_AUTHKEYS + "WHERE" + "HASH" + "= '" + hash + "';");
            if (result.next())
                return new DBAuthKey(hash,
                        UUID.fromString(result.getString("UUID")),
                        result.getString("PERM_GROUP"),
                        result.getString("STUDY_GROUP"),
                        result.getString("PARTY_GROUP"),
                        result.getString("UNTIL"),
                        result.getString("META")
                );

            return null;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            getPlugin().criticalError("Error occurred while getting SQL auth key");

            return null;
        }
    }

//    public @Nullable
//    DBParticipant getParticipant(UUID uuid) {
//        try {
//
//            Statement statement = connection.createStatement();
//            ResultSet result = statement.executeQuery("SELECT * FROM" + CONST_TABLE_NAME_PARTICIPANTS + "WHERE" + "UUID" + "= " + uuid.toString() + ";");
//            if (result.next())
//                return new DBParticipant(uuid, result.getString("STUDY_GROUP"), result.getString("PARTY_GROUP"), result.getString("META"));
//
//            return null;
//
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//            getPlugin().criticalError("Error occurred while getting SQL participant");
//
//            return null;
//        }
//    }

    public @Nullable
    UUID getRegCodeUUID(String regCode) {
        try {

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM" + CONST_TABLE_NAME_REGCODES + "WHERE" + "CODE" + "='" + regCode + "';");
            if (result.next())
                return UUID.fromString(result.getString("UUID"));

            return null;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            getPlugin().criticalError("Error occurred while getting SQL reg code uuid");

            return null;
        }
    }

    public void addRegCode(String regCode, UUID uniqueID) {
        executeUpdateAsync("INSERT INTO " + CONST_TABLE_NAME_REGCODES + " (CODE, UUID) VALUES (" +
                "'" + regCode + "', " +
                "'" + uniqueID + "'" +
                ");");
    }

    public void addAuthPlayer(DBAuthPlayer player) {
        executeUpdateAsync("INSERT INTO " + CONST_TABLE_NAME_PLAYERS + " (UUID, DISCORD_ID, KEY_HASH) VALUES (" +
                "'" + player.getUUID() + "', " +
                "'" + player.getDiscordID() + "', " +
                "'" + player.getKeyHash() + "'" +
                ");");
    }

    public void addAuthPlayer(UUID uuid, String discordID) {
        executeUpdateAsync("INSERT INTO " + CONST_TABLE_NAME_PLAYERS + " (UUID, DISCORD_ID, KEY_HASH) VALUES (" +
                "'" + uuid + "', " +
                "'" + discordID + "', " +
                      "NULL" +
                ");");
    }

    public void addAuthKey(DBAuthKey key) {
        executeUpdateAsync("INSERT INTO " + CONST_TABLE_NAME_AUTHKEYS + " (HASH, UUID, PERM_GROUP, STUDY_GROUP, PARTY_GROUP, UNTIL, META) VALUES (" +
                "'" + key.getHash() + "', " +
                "'" + key.getUUID() + "', " +
                "'" + key.getPermGroup() + "', " +
                "'" + key.getStudyGroup() + "', " +
                "'" + key.getPartyGroup() + "', " +
                "'" + key.getUntilString() + "', " +
                "'" + key.getMetaRaw() + "'" +
                ");");
    }

    public void deleteAuthKey(String hash, UUID uniqueID) {
        executeUpdateAsync("UPDATE " + CONST_TABLE_NAME_PLAYERS + " SET KEY_HASH = '" + hash + "' WHERE UUID = '" + uniqueID + "';");
        executeUpdateAsync("DELETE FROM " + CONST_TABLE_NAME_AUTHKEYS + " WHERE HASH='" + hash + "';");
    }

//    public void addParticipant(DBParticipant participant) {
//        executeUpdateAsync("INSERT INTO " + CONST_TABLE_NAME_PARTICIPANTS + " (UUID, STUDY_GROUP, PARTY_GROUP, META) VALUES (" +
//                "'" + participant.getUUID() + "', " +
//                "'" + participant.getStudyGroup() + "', " +
//                "'" + participant.getPartyGroup() + "', " +
//                "'" + participant.getMeta() + "'" +
//                ");");
//    }

    public void setAuthPlayerAuthKey(UUID uuid, String hash) {
        executeUpdateAsync("UPDATE " + CONST_TABLE_NAME_PLAYERS + " SET KEY_HASH = '" + hash + "' WHERE UUID = '" + uuid + "';");
        executeUpdateAsync("UPDATE " + CONST_TABLE_NAME_AUTHKEYS + " SET UUID = '" + hash + "' WHERE HASH = '" + uuid + "';");
    }

    // Closes the connection
    public void closeConnection() {
        try {

            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            getPlugin().criticalError("Error occurred while closing SQL connection");
        }
    }

    // Opens a connection
    private void openConnection() {
        try {

            if (connection != null && !connection.isClosed()) {
                return;
            }

            synchronized (this) {
                if (connection != null && !connection.isClosed()) {
                    return;
                }
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + CONST_HOST + ":" + CONST_PORT + "/" + CONST_DATABASE, CONST_USERNAME, CONST_PASSWORD);
            }

        } catch (ClassNotFoundException | SQLException throwables) {
            throwables.printStackTrace();
            getPlugin().criticalError("Error occurred while opening SQL connection");
        }
    }

    private void executeUpdateAsync(String update) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {

                    Statement statement = connection.createStatement();
                    statement.executeUpdate(update);

                } catch (
                        SQLException throwables) {
                    throwables.printStackTrace();
                    getPlugin().criticalError("Error occurred while executing async update");
                }
            }
        }.runTaskAsynchronously(getPlugin());
    }

    // Prepares SQL tables
    private void setupTables() {
        try {

            Statement statement = connection.createStatement();
            String sqlStatement = "";

            if (!connection.getMetaData().getTables(null, null, CONST_TABLE_NAME_PLAYERS, null).next()) {
                sqlStatement = "CREATE TABLE IF NOT EXISTS " + CONST_TABLE_NAME_PLAYERS
                        + "  (UUID           CHAR(36),"
                        + "   DISCORD_ID            CHAR(18),"
                        + "   KEY_HASH          CHAR(64));";
                statement.execute(sqlStatement);

                sqlStatement = "CREATE UNIQUE INDEX UUID_INDEX ON " + CONST_TABLE_NAME_PLAYERS + " (UUID);";
                statement.execute(sqlStatement);
            }

//            sqlStatement = "CREATE TABLE IF NOT EXISTS " + CONST_TABLE_PREFIX + CONST_TABLE_NAME_PARTICIPANTS
//                    + "  (UUID           CHAR(36),"
//                    + "   STUDY_GROUP            VARCHAR(16),"
//                    + "   PARTY_GROUP          VARCHAR(16),"
//                    + "   META          VARCHAR(256))";
//            statement.execute(sqlStatement);
//
//            sqlStatement = "CREATE UNIQUE INDEX " + CONST_TABLE_NAME_PARTICIPANTS + "_UUID_INDEX ON " + CONST_TABLE_NAME_PARTICIPANTS + " (UUID);";
//            statement.execute(sqlStatement);

//            sqlStatement = "CREATE TABLE IF NOT EXISTS " + CONST_TABLE_PREFIX + CONST_TABLE_NAME_AUTHKEYS
//                    + "  (HASH           CHAR(64),"
//                    + "   GROUP            VARCHAR(16),"
//                    + "   UNTIL          CHAR(8),"
//                    + "   UUID          CHAR(36))";
//            statement.execute(sqlStatement);

            if (!connection.getMetaData().getTables(null, null, CONST_TABLE_NAME_AUTHKEYS, null).next()) {
                sqlStatement = "CREATE TABLE IF NOT EXISTS " + CONST_TABLE_NAME_AUTHKEYS
                        + "  (HASH           CHAR(64),"
                        + "   UUID          CHAR(36),"
                        + "   PERM_GROUP            VARCHAR(16),"
                        + "   STUDY_GROUP            VARCHAR(16),"
                        + "   PARTY_GROUP            VARCHAR(16),"
                        + "   UNTIL          CHAR(8),"
                        + "   META          VARCHAR(256));";
                statement.execute(sqlStatement);

                sqlStatement = "CREATE UNIQUE INDEX HASH_INDEX ON " + CONST_TABLE_NAME_AUTHKEYS + " (HASH);";
                statement.execute(sqlStatement);
            }

            if (!connection.getMetaData().getTables(null, null, CONST_TABLE_NAME_REGCODES, null).next()) {
                sqlStatement = "CREATE TABLE IF NOT EXISTS " + CONST_TABLE_NAME_REGCODES
                        + "   (CODE          CHAR(4),"
                        + "   UUID            CHAR(36));";
                statement.execute(sqlStatement);

                sqlStatement = "CREATE UNIQUE INDEX CODE_INDEX ON " + CONST_TABLE_NAME_REGCODES + " (CODE);";
                statement.execute(sqlStatement);
            }
            executeUpdateAsync("TRUNCATE TABLE " + CONST_TABLE_NAME_REGCODES + ";");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            getPlugin().criticalError("Error occurred while setting up SQL tables");
        }
    }

    @Override
    public void onDisable() {
        closeConnection();
    }

}
