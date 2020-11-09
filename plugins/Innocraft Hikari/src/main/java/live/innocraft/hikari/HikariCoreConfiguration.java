package live.innocraft.hikari;

import live.innocraft.hikari.PluginCore.HikariPlugin;
import live.innocraft.hikari.PluginCore.HikariPluginConfiguration;

import java.util.HashSet;

public class HikariCoreConfiguration extends HikariPluginConfiguration {

    private String serverType;

    public HikariCoreConfiguration(HikariPlugin plugin) {
        super(plugin, "common.yml", true);
    }

    public String getServerType() {
        return serverType;
    }

    @Override
    public void onReload() {
        if (getCfgFile().contains("server-type"))
            serverType = getCfgFile().getString("server-type");
        else
            serverType = "other";
    }

    @Override
    public void onLateInitialization() {

    }

}
