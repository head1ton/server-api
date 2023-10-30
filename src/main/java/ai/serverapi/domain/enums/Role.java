package ai.serverapi.domain.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    MEMBER("MEMBER", "MEMBER"),
    SELLER("SELLER", "MEMBER, SELLER"),
    ADMIN("ADMIN", "MEMBER, SELLER, ADMIN"),
    ;

    public final String roleName;
    public final String roleList;
}
