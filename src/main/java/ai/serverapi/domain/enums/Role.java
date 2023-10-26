package ai.serverapi.domain.enums;

public enum Role {
    USER("USER", "일반 유저"),
    SELLER("SELLER", "판매자 유저"),
    ;

    public final String ROLE;
    public final String ROLE_VALUE;

    Role(final String role, final String role_value) {
        this.ROLE = role;
        this.ROLE_VALUE = role_value;
    }
}
