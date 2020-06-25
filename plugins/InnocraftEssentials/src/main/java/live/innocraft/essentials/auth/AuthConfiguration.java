package live.innocraft.essentials.auth;

import live.innocraft.essentials.core.Essentials;
import live.innocraft.essentials.core.EssentialsConfiguration;

public class AuthConfiguration extends EssentialsConfiguration {

    private String defaultJoinServer = null;

    public AuthConfiguration(Essentials plugin) {
        super(plugin, "auth.yml", true);
    }

    @Override
    public void onReload() {
        defaultJoinServer = getCfgFile().getString("default-join-server");
    }

    public String getDefaultJoinServer() {
        return defaultJoinServer;
    }
}
