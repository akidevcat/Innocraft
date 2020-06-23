package live.innocraft.essentials.authkeys;

public class AuthKeyGroup {

    private String[] roles;
    private String[] groups;

    public AuthKeyGroup(String[] roles, String[] groups) {
        this.roles = roles;
        this.groups = groups;
    }

    public String[] getRoles() {
        return roles;
    }

    public String[] getGroups() {
        return groups;
    }

}
