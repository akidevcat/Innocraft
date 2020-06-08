package live.innocraft.essentialsbungee.ustudy;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Date;

public class UStudyUpdate implements Runnable {

    private final UStudy core;

    public UStudyUpdate(UStudy core) {
        this.core = core;
    }

    @Override
    public void run() {
        long time = (new Date()).getTime();
        for (UStudyRestrictedServer s : core.restrictedServers) {
            if (s.getActiveSession() >= 1000 * 60)
                s.allowConnection();
            else {
                s.restrictConnection();
                ServerInfo si = core.getPlugin().getProxy().getServerInfo(s.getName());
                if (si != null) {
                    for (ProxiedPlayer p : si.getPlayers()) {
                        if (!p.hasPermission("innocraft.serverbypass"))
                            p.connect(core.getPlugin().getProxy().getServerInfo("general"));
                    }
                }
            }
        }
    }

}
