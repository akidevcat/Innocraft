package live.innocraft.essentialsbungee.ustudy;

import live.innocraft.essentialsbungee.EssentialsBungee;
import live.innocraft.essentialsbungee.EssentialsModule;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class UStudy extends EssentialsModule {

    protected final HashSet<UStudyRestrictedServer> restrictedServers;

    public UStudy(EssentialsBungee plugin) {
        super(plugin);

        restrictedServers = new HashSet<>();
        restrictedServers.add(new UStudyRestrictedServer("excursion"));
        restrictedServers.add(new UStudyRestrictedServer("game"));
        restrictedServers.add(new UStudyRestrictedServer("anarchy"));
    }

    @Override
    public void LateInitialization() {
        getPlugin().getProxy().getScheduler().schedule(getPlugin(), new UStudyUpdate(this), 10L, 10L, TimeUnit.SECONDS);
    }

    public void SendRestrictionInvalidationRequestAll() {
        for (UStudyRestrictedServer s : restrictedServers) {
            s.invalidate();
        }
    }

    public boolean getServerRestrictionState(String name) {
        for (UStudyRestrictedServer s : restrictedServers) {
            if (s.getName().equals(name))
                return s.getServerRestrictionState();
        }
        return false;
    }

}
