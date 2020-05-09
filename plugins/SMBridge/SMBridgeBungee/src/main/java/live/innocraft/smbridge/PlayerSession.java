package live.innocraft.smbridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class PlayerSession {
    public String ip;
    public Long date;

    public PlayerSession (String ip) {
        this.ip = ip;
        this.date = System.currentTimeMillis();
    }

    public PlayerSession (String ip, Long date) {
        this.ip = ip;
        this.date = date;
    }

    public static List<String> Sessions2List(Map<String, PlayerSession> sessions) {
        List<String> result = new ArrayList<String>();

        for (Map.Entry<String, PlayerSession> entry : sessions.entrySet()) {
            String name = entry.getKey();
            PlayerSession session = entry.getValue();
            String e = name + " " + session.ip + " " + session.date;
            result.add(e);
        }

        return result;
    }

    public static Map<String, PlayerSession> List2Sessions(List<String> sessions) {
        Map<String, PlayerSession> result = new HashMap<String, PlayerSession>();

        for (String entry : sessions) {
            String[] splitted = entry.split(" ");
            String name = splitted[0];
            String ip = splitted[1];
            Long date = Long.parseLong(splitted[2]);
            result.put(name, new PlayerSession(ip, date));
        }

        return result;
    }
}
