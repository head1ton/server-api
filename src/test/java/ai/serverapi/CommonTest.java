package ai.serverapi;

public enum CommonTest {
    MEMBER_EMAIL("member@gmail.com"),
    SELLER_EMAIL("seller@gmail.com"),
    SELLER2_EMAIL("seller2@gmail.com"),
    PASSWORD("password"),
    ;
    private final String val;

    CommonTest(final String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
