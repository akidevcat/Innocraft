package live.innocraft.essentials.authkeys;

public class AuthKeyPermGroup {

    private String[] roles;
    private String perm;

    public AuthKeyPermGroup(String[] roles, String perm) {
        this.roles = roles;
        this.perm = perm;
    }

    public String[] getRoles() {
        return roles;
    }

    public String getPerm() {
        return perm;
    }

}
