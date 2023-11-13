package ai.serverapi.member.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemberRole {
    MEMBER("MEMBER", "MEMBER"),
    SELLER("SELLER", "MEMBER, SELLER"),
    ADMIN("ADMIN", "MEMBER, SELLER, ADMIN"),
    ;

    public final String roleName;
    public final String roleList;
}
