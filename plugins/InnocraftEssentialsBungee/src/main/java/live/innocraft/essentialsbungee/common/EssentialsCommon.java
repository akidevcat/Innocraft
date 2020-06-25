package live.innocraft.essentialsbungee.common;

import live.innocraft.essentialsbungee.core.EssentialsBungee;
import live.innocraft.essentialsbungee.core.EssentialsModule;

import java.util.UUID;

public class EssentialsCommon extends EssentialsModule {

    public EssentialsCommon(EssentialsBungee plugin) {
        super(plugin);
    }

    public void bridgeChangePlayerServerForce(String uniqueID, String serverName) {
        getPlugin().getProxy().getPlayer(UUID.fromString(uniqueID)).connect(getPlugin().getProxy().getServerInfo(serverName));
    }

}
