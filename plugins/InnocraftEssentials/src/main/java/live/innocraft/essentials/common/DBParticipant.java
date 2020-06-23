package live.innocraft.essentials.common;

import java.util.UUID;

public class DBParticipant {

    private UUID uuid;
    private String study_group;
    private String party_group;
    private String meta;

    public DBParticipant (UUID uuid, String study_group, String party_group, String meta) {
        this.uuid = uuid;
        this.study_group = study_group;
        this.party_group = party_group;
        this.meta = meta;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getStudyGroup() {
        return study_group;
    }

    public String getPartyGroup() {
        return party_group;
    }

    public String getMeta() {
        return meta;
    }
}
