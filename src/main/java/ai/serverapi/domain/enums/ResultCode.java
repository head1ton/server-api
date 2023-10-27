package ai.serverapi.domain.enums;

public enum ResultCode {
    SUCCESS("0000", "success"),
    POST("0001", "201 success"),
//    BAD_REQUEST("0400", "bad request"),
//    UNAUTHORIZED("0401", "unauthorized"),
//    FORBIDDEN("0403", "forbidden"),
//    FAIL("9999", "fail"),
    ;

    public final String CODE;
    public final String MESSAGE;
    ResultCode(String CODE, String MESSAGE) {
        this.CODE = CODE;
        this.MESSAGE = MESSAGE;
    }
}
