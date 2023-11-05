package ai.serverapi;

import ai.serverapi.config.redis.RestdocsConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestdocsConfig.class)
@ExtendWith(RestDocumentationExtension.class)
public class ControllerBaseTest extends BaseTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected RestDocumentationResultHandler docs;

    protected ObjectMapper objectMapper = new ObjectMapper();

    protected String MEMBER_EMAIL = "member@appling.com";
    protected String SELLER_EMAIL = "seller@appling.com";
    protected String SELLER2_EMAIL = "seller2@appling.com";
    protected String PASSWORD = "password";

    @BeforeEach
    void setUp(final WebApplicationContext context,
        final RestDocumentationContextProvider provider) throws Exception {
        this.mock = MockMvcBuilders.webAppContextSetup(context)
                                   .apply(
                                       MockMvcRestDocumentation.documentationConfiguration(provider)
                                                               .uris()
                                                               .withScheme("http")
                                                               .withHost("127.0.0.1")
                                                               .withPort(80)
                                   )  // rest docs 설정 주입
                                   .alwaysDo(
                                       MockMvcResultHandlers.print())
                                   .alwaysDo(docs)
                                   .addFilters(
                                       new CharacterEncodingFilter("UTF-8", true) // 한글 깨짐 방지
                                   )
                                   .build();
    }
}
