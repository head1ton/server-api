package ai.serverapi.global.base;

public enum ResultCode {
    SUCCESS("0000", "success"),
    POST("0001", "201 success"),
//    BAD_REQUEST("0400", "bad request"),
//    UNAUTHORIZED("0401", "unauthorized"),
//    FORBIDDEN("0403", "forbidden"),
//    FAIL("9999", "fail"),
    ;

    public final String code;
    public final String message;

    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
