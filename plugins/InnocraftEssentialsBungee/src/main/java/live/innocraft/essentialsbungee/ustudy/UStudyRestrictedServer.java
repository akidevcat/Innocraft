package live.innocraft.essentialsbungee.ustudy;

import java.util.Date;

public class UStudyRestrictedServer implements Comparable<UStudyRestrictedServer> {

    private final String name;
    private boolean isRestricted = false;
    private long lastInvalidationTime = 0;

    public UStudyRestrictedServer(String name) {
        this.name = name;
    }

    public boolean getServerRestrictionState() {
        return isRestricted;
    }

    public void restrictConnection() {
        isRestricted = true;
    }

    public void allowConnection() {
        isRestricted = false;
    }

    public void invalidate() {
        restrictConnection();
        lastInvalidationTime = (new Date()).getTime();
    }

    public long getActiveSession() {
        return (new Date()).getTime() - lastInvalidationTime;
    }

    @Override
    public int compareTo(UStudyRestrictedServer other){
        return name.compareTo(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public String getName() {
        return name;
    }
}
