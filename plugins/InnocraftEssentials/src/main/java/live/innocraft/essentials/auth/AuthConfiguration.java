package live.innocraft.essentials.auth;

import live.innocraft.essentials.core.Essentials;
import live.innocraft.essentials.core.EssentialsConfiguration;

public class AuthConfiguration extends EssentialsConfiguration {

    private String defaultJoinServer = null;
    private boolean noKeysNeeded = false;

    public AuthConfiguration(Essentials plugin) {
        super(plugin, "auth.yml", true);
    }

    @Override
    public void onReload() {
        defaultJoinServer = getCfgFile().getString("default-join-server");
        noKeysNeeded = getCfgFile().getBoolean("no-keys-needed");
    }

    public String getDefaultJoinServer() {
        return defaultJoinServer;
    }

    public boolean isNoKeysNeeded() {
        return noKeysNeeded;
    }
}
