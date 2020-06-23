package live.innocraft.essentials.common;

import live.innocraft.essentials.core.EssentialsConfiguration;
import live.innocraft.essentials.core.Essentials;

import java.util.HashSet;

public class CommonConfiguration extends EssentialsConfiguration {

    private ServerType serverType;
    private final HashSet<String> disabledModules;

    public CommonConfiguration(Essentials plugin) {
        super(plugin, "common.yml", true);

        disabledModules = new HashSet<>();
    }

    public ServerType getServerType() {
        return serverType;
    }

    public boolean getModuleState(String moduleName) {
        return !disabledModules.contains(moduleName);
    }

    @Override
    public void onReload() {
        if (getCfgFile().contains("disabled-modules"))
            disabledModules.addAll(getCfgFile().getStringList("disabled-modules"));
        if (getCfgFile().contains("server-type")) {
            try {
                serverType = ServerType.valueOf(getCfgFile().getString("server-type"));
            } catch (IllegalArgumentException ex) {
                serverType = ServerType.other;
            }
        }
        else
            serverType = ServerType.other;
    }

    @Override
    public void onLateInitialization() {

    }
}
