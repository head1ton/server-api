package ai.serverapi.common.controller;

import static ai.serverapi.Base.SELLER_EMAIL;
import static ai.serverapi.Base.SELLER_LOGIN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ai.serverapi.RestdocsBaseTest;
import ai.serverapi.global.s3.S3Service;
import ai.serverapi.member.domain.entity.MemberEntity;
import ai.serverapi.member.enums.IntroduceStatus;
import ai.serverapi.member.repository.IntroduceJpaRepository;
import ai.serverapi.member.repository.MemberJpaRepository;
import ai.serverapi.member.repository.SellerJpaRepository;
import ai.serverapi.member.service.MemberAuthService;
import ai.serverapi.product.domain.entity.IntroduceEntity;
import ai.serverapi.product.domain.entity.SellerEntity;
import ai.serverapi.product.repository.CategoryJpaRepository;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@SqlGroup({
    @Sql(scripts = {"/sql/init.sql",
        "/sql/introduce.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
})
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@Execution(ExecutionMode.CONCURRENT)
class CommonControllerDocs extends RestdocsBaseTest {

    private final String PREFIX = "/api/common";
    @MockBean
    private S3Service s3Service;
    @Autowired
    private MemberAuthService memberAuthService;
    @Autowired
    private MemberJpaRepository memberJpaRepository;
    @Autowired
    private SellerJpaRepository sellerJpaRepository;
    @Autowired
    private IntroduceJpaRepository introduceJpaRepository;
    @Autowired
    private CategoryJpaRepository categoryJpaRepository;

    @AfterEach
    void cleanUp() {
        categoryJpaRepository.deleteAll();
        introduceJpaRepository.deleteAll();
        sellerJpaRepository.deleteAll();
        memberJpaRepository.deleteAll();
    }

    @Test
    @DisplayName(PREFIX + "/image")
    void uploadImage() throws Exception {

        List<String> list = new LinkedList<>();
        list.add("image/2/20231029/203600_1.png");
        given(s3Service.putObject(anyString(), anyString(), any())).willReturn(list);

        ResultActions perform = mock.perform(
            multipart(PREFIX + "/image")
                .file(new MockMultipartFile("image", "text1.png",
                    MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                    "123".getBytes(
                        StandardCharsets.UTF_8)))
                .header(AUTHORIZATION, "Bearer " + SELLER_LOGIN.getAccessToken())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );

        perform.andExpect(status().is2xxSuccessful());

        perform.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token")
            ),
            requestParts(
                partWithName("image").description("업로드 이미지")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.url").type(JsonFieldType.STRING)
                                               .description("업로드 된 이미지 url")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/html")
    void uploadHtml() throws Exception {

        List<String> list = new LinkedList<>();
        list.add("html/1/20230815/172623_1.html");
        given(s3Service.putObject(anyString(), anyString(), any())).willReturn(list);

        ResultActions perform = mock.perform(
            multipart(PREFIX + "/html")
                .file(new MockMultipartFile("html", "text1.html",
                    MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                    "123".getBytes(
                        StandardCharsets.UTF_8)))
                .header(AUTHORIZATION, "Bearer " + SELLER_LOGIN.getAccessToken())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );

        perform.andExpect(status().is2xxSuccessful());

        perform.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token")
            ),
            requestParts(
                partWithName("html").description("업로드 html 파일")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.url").type(JsonFieldType.STRING)
                                         .description("업로드 된 html url")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/introduce (GET)")
    void getSellerIntroduce() throws Exception {
        MemberEntity memberEntity = memberJpaRepository.findByEmail(SELLER_EMAIL).get();
        SellerEntity sellerEntity = sellerJpaRepository.findByMember(memberEntity).get();
        introduceJpaRepository.save(IntroduceEntity.of(sellerEntity, "",
            "https://cherryandplum.s3.ap-northeast-2.amazonaws.com/html/1/20230815/172623_0.html",
            IntroduceStatus.USE));

        given(s3Service.getObject(anyString(), anyString())).willReturn(
            "<!doctype html>\n" +
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "\t<title>watermelon</title>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "\t<H2>example 1-2</H2>\n" +
                "\t<HR>\n" +
                "\texample 1-2\n" +
                "</body>\n" +
                "\n" +
                "</html>");

        ResultActions resultActions = mock.perform(
            get(PREFIX + "/introduce/{seller_id}", sellerEntity.getId())
        );

        resultActions.andExpect(status().is2xxSuccessful());

        resultActions.andDo(docs.document(
            pathParameters(
                parameterWithName("seller_id").description("seller_id")
            )
        ));
    }
}
