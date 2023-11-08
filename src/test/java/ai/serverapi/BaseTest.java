package ai.serverapi;

import ai.serverapi.member.domain.Introduce;
import ai.serverapi.member.domain.Member;
import ai.serverapi.member.domain.Seller;
import ai.serverapi.member.dto.request.JoinRequest;
import ai.serverapi.member.enums.IntroduceStatus;
import ai.serverapi.member.enums.Role;
import ai.serverapi.member.repository.IntroduceRepository;
import ai.serverapi.member.repository.MemberRepository;
import ai.serverapi.member.repository.SellerRepository;
import ai.serverapi.product.domain.Category;
import ai.serverapi.product.enums.CategoryStatus;
import ai.serverapi.product.repository.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
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

    protected String BEAUTY_CATEGORY = "화장품";
    protected String HEALTH_CATEGORY = "건강식품";
    protected String LIFE_CATEGORY = "생활용품";

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

    @Transactional
    @BeforeAll
    void setUp() {
        String[] emailList = {MEMBER_EMAIL, SELLER_EMAIL, SELLER2_EMAIL};
        for (String email : emailList) {
            Optional<Member> findMember = memberRepository.findByEmail(email);
            if (findMember.isEmpty()) {
                JoinRequest joinRequest = new JoinRequest(email, passwordEncoder.encode(PASSWORD),
                    "name",
                    "nick", "19941030");
                Member saveMember = memberRepository.save(Member.of(joinRequest));
                if (saveMember.getEmail().equals(SELLER_EMAIL) || saveMember.getEmail().equals(
                    SELLER2_EMAIL)) {
                    if (saveMember.getEmail().equals(SELLER_EMAIL)) {
                        saveMember.patchMemberRole(Role.SELLER);
                        Seller seller = sellerRepository.save(
                            Seller.of(saveMember, "넷플릭스", "01012341234", "1234",
                                "서울특별시 강남구 백두산길 128", "상세 주소",
                                "email@gmail.com"));
                    }
                    if (saveMember.getEmail().equals(SELLER2_EMAIL)) {
                        saveMember.patchMemberRole(Role.SELLER);
                        Seller seller = sellerRepository.save(
                            Seller.of(saveMember, "디즈니TV", "01012341234", "1234",
                                "부산광역시 동래구 한라산길 128", "상세 주소",
                                "email@gmail.com"));
                        introduceRepository.save(Introduce.of(seller, "한라산길",
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
    }
}
