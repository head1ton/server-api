package ai.serverapi.domain.enums;

public enum Role {
    MEMBER("MEMBER", "MEMBER"),
    SELLER("SELLER", "MEMBER, SELLER"),
    ADMIN("ADMIN", "MEMBER, SELLER, ADMIN"),
    ;

    public final String roleName;
    public final String roleList;

    Role(final String role, final String role_value) {
        this.roleName = role;
        this.roleList = role_value;
    }
}
