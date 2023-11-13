package ai.serverapi;

import ai.serverapi.member.controller.response.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Base {

    public static final Long CATEGORY_ID_BEAUTY = 1L;
    public static final Long CATEGORY_ID_HEALTH = 2L;
    public static final Long CATEGORY_ID_NORMAl = 3L;
    public static final Long PRODUCT_ID_MASK = 1L;
    public static final Long PRODUCT_ID_PEAR = 2L;
    public static final Long PRODUCT_ID_NORMAL = 3L;

    public static final Long PRODUCT_OPTION_ID_MASK = 1L;
    public static final Long PRODUCT_OPTION_ID_PEAR = 2L;
    public static final Long PRODUCT_OPTION_ID_NORMAL = 3L;
    public static ObjectMapper objectMapper = new ObjectMapper();
    public static String MEMBER_EMAIL = "member@gmail.com";
    public static String SELLER_EMAIL = "seller@gmail.com";
    public static String SELLER2_EMAIL = "seller2@gmail.com";
    public static String PASSWORD = "password";
    public static LoginResponse MEMBER_LOGIN = new LoginResponse("", "member-test-token",
        "refresh-token", 1000L, 1000L);
    public static LoginResponse SELLER_LOGIN = new LoginResponse("", "seller-test-token",
        "refresh-token", 1000L, 1000L);
    public static LoginResponse SELLER2_LOGIN = new LoginResponse("", "seller2-test-token",
        "refresh-token", 1000L, 1000L);
}
