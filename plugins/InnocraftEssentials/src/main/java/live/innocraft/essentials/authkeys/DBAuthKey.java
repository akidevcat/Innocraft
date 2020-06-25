package live.innocraft.essentials.authkeys;

import live.innocraft.essentials.helper.EssentialsHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DBAuthKey {

    private final String hash;
    private UUID uuid;
    private String perm_group;
    private String study_group;
    private String party_group;
    private Date until;
    private String meta;

    public DBAuthKey(String hash) {
        this.hash = hash;
        this.uuid = null;
        this.perm_group = null;
        this.study_group = null;
        this.party_group = null;
        this.until = new Date(0);
        this.meta = null;
    }

    public DBAuthKey(String hash, String uuid, String perm_group, String study_group, String party_group, String until, String meta) {
        this.hash = EssentialsHelper.parseDBString(hash);
        this.uuid = EssentialsHelper.parseDBUniqueID(uuid);
        this.perm_group = EssentialsHelper.parseDBString(perm_group);
        this.study_group = EssentialsHelper.parseDBString(study_group);
        this.party_group = EssentialsHelper.parseDBString(party_group);
        this.until = EssentialsHelper.parseDBDate(until);
        this.meta = EssentialsHelper.parseDBString(meta);
    }

    public DBAuthKey(String hash, String perm_group, String study_group, String party_group, String until, String meta) {
        this.hash = EssentialsHelper.parseDBString(hash);
        this.uuid = null;
        this.perm_group = EssentialsHelper.parseDBString(perm_group);
        this.study_group = EssentialsHelper.parseDBString(study_group);
        this.party_group = EssentialsHelper.parseDBString(party_group);
        this.until = EssentialsHelper.parseDBDate(until);
        this.meta = EssentialsHelper.parseDBString(meta);
    }

    public static DBAuthKey createEmpty(String key) {
        return new DBAuthKey(EssentialsHelper.HashSHA256(key));
    }

    public DBAuthKey setUntil(String date) {
        try {
            this.until = new SimpleDateFormat("ddMMyyyy").parse(date);
        } catch (ParseException e) {
            this.until = new Date(0);
        }
        return this;
    }

    public DBAuthKey setUniqueID(UUID uniqueID) {
        this.uuid = uniqueID;
        return this;
    }

    public DBAuthKey setPermGroup(String group) {
        this.perm_group = group;
        return this;
    }

    public DBAuthKey setStudyGroup(String group) {
        this.study_group = group;
        return this;
    }

    public DBAuthKey setPartyGroup(String group) {
        this.party_group = group;
        return this;
    }

    public DBAuthKey setMetaRaw(String metaRaw) {
        this.meta = metaRaw;
        return this;
    }

    public String getHash() {
        return hash;
    }

    public Date getUntil() {
        return until;
    }

    public String getUntilString() {
        return new SimpleDateFormat("ddMMyyyy").format(until);
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getPermGroup() {
        return perm_group;
    }

    public String getStudyGroup() {
        return study_group;
    }

    public String getPartyGroup() {
        return party_group;
    }

    public String getMetaRaw() {
        return meta;
    }
}
