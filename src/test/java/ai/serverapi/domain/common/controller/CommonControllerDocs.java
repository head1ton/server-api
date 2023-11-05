package ai.serverapi.domain.common.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ai.serverapi.ControllerBaseTest;
import ai.serverapi.config.s3.S3Service;
import ai.serverapi.domain.member.dto.LoginDto;
import ai.serverapi.domain.member.service.MemberAuthService;
import ai.serverapi.domain.member.vo.LoginVo;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CommonControllerDocs extends ControllerBaseTest {

    private final String PREFIX = "/api/common";
    @Autowired
    private MemberAuthService memberAuthService;
    @MockBean
    private S3Service s3Service;

    @Test
    @DisplayName(PREFIX + "/image")
    void uploadImage() throws Exception {
        LoginDto loginDto = new LoginDto(SELLER_EMAIL, PASSWORD);
        LoginVo loginVo = memberAuthService.login(loginDto);
        List<String> list = new LinkedList<>();
        list.add("image/2/20231029/203600_1.txt");
        BDDMockito.given(s3Service.putObject(anyString(), anyString(), any())).willReturn(list);

        ResultActions perform = mockMvc.perform(
            multipart(PREFIX + "/image")
                .file(new MockMultipartFile("image", "text1.txt", MediaType.APPLICATION_JSON_VALUE,
                    "123".getBytes(
                        StandardCharsets.UTF_8)))
                .header(AUTHORIZATION, "Bearer " + loginVo.accessToken())
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
                fieldWithPath("data.image_url").type(JsonFieldType.STRING)
                                               .description("업로드 된 이미지 url")
            )
        ));
    }
}
