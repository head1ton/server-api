package ai.serverapi.domain.enums;

public enum ResultCode {
    SUCCESS("0000", "success"),
    POST("0001", "201 success"),
    FAIL("9999", "fail"),
    ;

    public String CODE;
    public String MESSAGE;
    ResultCode(String CODE, String MESSAGE) {
        this.CODE = CODE;
        this.MESSAGE = MESSAGE;
    }
}
