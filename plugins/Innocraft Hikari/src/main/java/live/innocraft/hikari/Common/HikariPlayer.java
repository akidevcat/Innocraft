package live.innocraft.hikari.Common;

import java.util.HashMap;
import java.util.UUID;

public class HikariPlayer {

    /*
    Hikari player represents a unified player class for all the child plugins.
    It can be extended in real-time by other plugins
     */

    private final UUID uuid;
    private final HashMap<String, Object> parameters;

    public HikariPlayer (UUID uuid) {
        this.uuid = uuid;
        this.parameters = new HashMap<>();
    }

    public UUID getUUID() { return uuid; }

    public Object getParameter(String name) {
        return parameters.get(name);
    }

    public void setParameter(String name, Object parameter) {
        parameters.put(name, parameter);
    }

    public boolean hasParameter(String name) {
        return parameters.containsKey(name);
    }

}
