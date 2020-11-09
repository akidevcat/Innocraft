package live.innocraft.hikari.SQL;

import live.innocraft.hikari.HikariCore;
import live.innocraft.hikari.PluginCore.HikariPlugin;
import live.innocraft.hikari.PluginCore.HikariPluginModule;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.UUID;

public class HikariSQL extends HikariPluginModule {

    private final String CONST_HOST = "localhost";
    private final int CONST_PORT = 3306;
    private final String CONST_DATABASE = "innocraft";
    private final String CONST_USERNAME = "innocraft";
    private final String CONST_PASSWORD = "innocraft";

    private Connection connection;

    public HikariSQL(HikariPlugin plugin) {
        super(plugin);

        openConnection();
    }

    public void updateConnection() {
        try {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT 1");
        } catch (Exception ex) {
            ex.printStackTrace();
            closeConnection();
            openConnection();
        }
    }

    // Closes the connection
    public void closeConnection() {
        try {

            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            //getPlugin().criticalError("Error occurred while closing SQL connection");
        }
    }

    // Opens a connection
    public void openConnection() {
        try {

            if (connection != null && !connection.isClosed()) {
                return;
            }

            synchronized (this) {
                if (connection != null && !connection.isClosed()) {
                    return;
                }
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + CONST_HOST + ":" + CONST_PORT + "/" + CONST_DATABASE + "?autoReconnect=true", CONST_USERNAME, CONST_PASSWORD);
            }

        } catch (ClassNotFoundException | SQLException throwables) {
            throwables.printStackTrace();
            //getPlugin().criticalError("Error occurred while opening SQL connection");
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void executeUpdateAsync(String update) {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateConnection();
                try {
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(update);

                } catch (
                        SQLException throwables) {
                    throwables.printStackTrace();
                    //getPlugin().criticalError("Error occurred while executing async update");
                }
            }
        }.runTaskAsynchronously(getPlugin());
    }

    @Override
    public void onDisable() {
        closeConnection();
    }
}
