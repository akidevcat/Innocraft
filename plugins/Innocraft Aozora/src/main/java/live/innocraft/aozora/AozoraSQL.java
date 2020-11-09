package live.innocraft.aozora;

import live.innocraft.aozora.Structures.DBAuthPlayer;
import live.innocraft.hikari.HikariCore;
import live.innocraft.hikari.PluginCore.HikariPlugin;
import live.innocraft.hikari.PluginCore.HikariPluginModule;
import live.innocraft.hikari.SQL.HikariSQL;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class AozoraSQL extends HikariPluginModule {

    private final String CONST_TABLE_NAME_PLAYERS = "ic_players";
    private final String CONST_TABLE_NAME_REGCODES = "ic_regcodes";

    private final HikariSQL hikariSQL;

    public AozoraSQL(HikariPlugin plugin) {
        super(plugin);

        System.out.println(HikariCore.getInstance().isReady());
        hikariSQL = HikariCore.getInstance().getSQLModule();
    }

    // Gets an Authentication player from the database
    public @Nullable
    DBAuthPlayer getAuthPlayer(UUID uuid) {
        hikariSQL.updateConnection();

        if (uuid == null)
            return null;

        try {

            Statement statement = hikariSQL.getConnection().createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM " + CONST_TABLE_NAME_PLAYERS + " WHERE UUID = '" + uuid.toString() + "';");
            if (result.next())
                return new DBAuthPlayer(
                        uuid.toString(),
                        result.getString("DISCORD_ID"));

            return null;

        } catch (SQLException throwables) {
            throwables.printStackTrace();

            return null;
        }
    }

    public void addRegCode(String regCode, UUID uniqueID) {
        hikariSQL.executeUpdateAsync("INSERT INTO " + CONST_TABLE_NAME_REGCODES + " (CODE, UUID) VALUES (" +
                "'" + regCode + "', " +
                "'" + uniqueID + "'" +
                ");");
    }

    public void deleteRegCode(String code) {
        hikariSQL.executeUpdateAsync("DELETE FROM " + CONST_TABLE_NAME_REGCODES + " WHERE CODE='" + code + "';");
    }

    public @Nullable
    UUID getRegCodeUUID(String regCode) {
        hikariSQL.updateConnection();
        try {

            Statement statement = hikariSQL.getConnection().createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM " + CONST_TABLE_NAME_REGCODES + " WHERE " + "CODE" + " ='" + regCode + "';");
            if (result.next())
                return UUID.fromString(result.getString("UUID"));

            return null;

        } catch (SQLException throwables) {
            throwables.printStackTrace();

            return null;
        }
    }

    public void addAuthPlayer(UUID uuid, String discordID) {
        hikariSQL.executeUpdateAsync("INSERT INTO " + CONST_TABLE_NAME_PLAYERS + " (UUID, DISCORD_ID) VALUES (" +
                "'" + uuid + "', " +
                "'" + discordID + "'" +
                ");");
    }

    public @Nullable
    DBAuthPlayer getAuthPlayerByDiscord(String discordID) {
        hikariSQL.updateConnection();
        try {

            Statement statement = hikariSQL.getConnection().createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM " + CONST_TABLE_NAME_PLAYERS + " WHERE DISCORD_ID = '" + discordID + "';");
            if (result.next())
                return new DBAuthPlayer(
                        result.getString("UUID"),
                        discordID);

            return null;

        } catch (SQLException throwables) {
            throwables.printStackTrace();

            return null;
        }
    }

    public UUID deleteUser(String discordID) {
        DBAuthPlayer authPlayer = getAuthPlayerByDiscord(discordID);
        if (authPlayer == null)
            return null;
        hikariSQL.executeUpdateAsync("DELETE FROM " + CONST_TABLE_NAME_PLAYERS + " WHERE UUID='" + authPlayer.getUUID() + "';");
        return authPlayer.getUUID();
    }

}
