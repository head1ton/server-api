package ai.serverapi;

import ai.serverapi.member.domain.Introduce;
import ai.serverapi.member.domain.Member;
import ai.serverapi.member.domain.Seller;
import ai.serverapi.member.dto.request.JoinRequest;
import ai.serverapi.member.dto.request.LoginRequest;
import ai.serverapi.member.dto.response.LoginResponse;
import ai.serverapi.member.enums.IntroduceStatus;
import ai.serverapi.member.enums.Role;
import ai.serverapi.member.repository.IntroduceRepository;
import ai.serverapi.member.repository.MemberRepository;
import ai.serverapi.member.repository.SellerRepository;
import ai.serverapi.member.service.MemberAuthService;
import ai.serverapi.product.domain.Category;
import ai.serverapi.product.domain.Product;
import ai.serverapi.product.dto.request.ProductRequest;
import ai.serverapi.product.enums.CategoryStatus;
import ai.serverapi.product.enums.ProductStatus;
import ai.serverapi.product.repository.CategoryRepository;
import ai.serverapi.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Execution(ExecutionMode.SAME_THREAD)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
public class BaseTest {
    protected ObjectMapper objectMapper = new ObjectMapper();

    protected String MEMBER_EMAIL = "member@gmail.com";
    protected String SELLER_EMAIL = "seller@gmail.com";
    protected String SELLER2_EMAIL = "seller2@gmail.com";
    protected String PASSWORD = "password";

    protected Member MEMBER = null;
    protected Seller SELLER1 = null;
    protected Seller SELLER2 = null;
    protected Product PRODUCT1 = null;
    protected Product PRODUCT2 = null;

    protected String BEAUTY_CATEGORY = "화장품";
    protected String HEALTH_CATEGORY = "건강식품";
    protected String LIFE_CATEGORY = "생활용품";

    protected LoginResponse SELLER_LOGIN;
    protected LoginResponse SELLER2_LOGIN;
    protected LoginResponse MEMBER_LOGIN;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private IntroduceRepository introduceRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MemberAuthService memberAuthService;

    @Transactional
    @BeforeAll
    @Order(1)
    void setUp() {
        String[] emailList = {MEMBER_EMAIL, SELLER_EMAIL, SELLER2_EMAIL};
        for (String email : emailList) {
            Optional<Member> findMember = memberRepository.findByEmail(email);
            if (findMember.isEmpty()) {
                JoinRequest joinRequest = new JoinRequest(email, passwordEncoder.encode(PASSWORD),
                    "name",
                    "nick", "19941030");
                Member saveMember = memberRepository.save(Member.of(joinRequest));

                if (saveMember.getEmail().equals(MEMBER_EMAIL)) {
                    MEMBER = saveMember;
                }

                if (saveMember.getEmail().equals(SELLER_EMAIL) || saveMember.getEmail().equals(
                    SELLER2_EMAIL)) {
                    if (saveMember.getEmail().equals(SELLER_EMAIL)) {
                        saveMember.patchMemberRole(Role.SELLER);
                        SELLER1 = sellerRepository.save(
                            Seller.of(saveMember, "넷플릭스", "01012341234", "1234",
                                "서울특별시 강남구 백두산길 128", "상세 주소",
                                "email@gmail.com"));
                    }
                    if (saveMember.getEmail().equals(SELLER2_EMAIL)) {
                        saveMember.patchMemberRole(Role.SELLER);
                        SELLER2 = sellerRepository.save(
                            Seller.of(saveMember, "디즈니TV", "01012341234", "1234",
                                "부산광역시 동래구 한라산길 128", "상세 주소",
                                "email@gmail.com"));
                        introduceRepository.save(Introduce.of(SELLER2, "한라산길",
                            "https://cherryandplum.s3.ap-northeast-2.amazonaws.com/html/1/20230815/172623_0.html",
                            IntroduceStatus.USE));
                    }
                }
            }
        }

        String[] categoryList = {BEAUTY_CATEGORY, HEALTH_CATEGORY, LIFE_CATEGORY};

        for (String category : categoryList) {
            Optional<Category> findCategory = categoryRepository.findByName(category);
            if (findCategory.isEmpty()) {
                categoryRepository.save(Category.of(category, CategoryStatus.USE));
            }
        }

        // 상품 등록
        Category category = categoryRepository.findByName(HEALTH_CATEGORY).get();
        ProductRequest productRequest1 = new ProductRequest(category.getId(), "테스트 상품1", "테스트",
            "테스트", "테스트 설명", 10000, 9000, "주의 사항", "원산지", "공급자", "이미지", "이미지1", "", "",
            ProductStatus.NORMAL.name(), 10, new ArrayList<>(), "normal");
        ProductRequest productRequest2 = new ProductRequest(category.getId(), "테스트 상품2", "테스트",
            "테스트", "테스트 설명", 10000, 9000, "주의 사항", "원산지", "공급자", "이미지", "이미지1", "", "",
            ProductStatus.NORMAL.name(), 10, new ArrayList<>(), "normal");
        PRODUCT1 = productRepository.save(Product.of(SELLER1, category, productRequest1));
        PRODUCT2 = productRepository.save(Product.of(SELLER2, category, productRequest2));

        LoginRequest sellerLoginRequest = new LoginRequest(SELLER_EMAIL, PASSWORD);
        SELLER_LOGIN = memberAuthService.login(sellerLoginRequest);

        LoginRequest sellerLoginRequest2 = new LoginRequest(SELLER2_EMAIL, PASSWORD);
        SELLER2_LOGIN = memberAuthService.login(sellerLoginRequest2);

        LoginRequest memberLoginRequest = new LoginRequest(MEMBER_EMAIL, PASSWORD);
        MEMBER_LOGIN = memberAuthService.login(memberLoginRequest);
    }
}
