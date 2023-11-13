package ai.serverapi.member.controller;

import static ai.serverapi.Base.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import ai.serverapi.RestdocsBaseTest;
import ai.serverapi.member.controller.request.JoinRequest;
import ai.serverapi.member.domain.entity.MemberEntity;
import ai.serverapi.member.repository.MemberJpaRepository;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Execution(ExecutionMode.CONCURRENT)
class AuthControllerTest extends RestdocsBaseTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;
    private final static String PREFIX = "/api/auth";

    @Test
    @DisplayName("중복 회원 가입 실패")
    void joinFail() throws Exception {
        JoinRequest joinRequest = JoinRequest.builder()
                                             .email("venus@mail.com")
                                             .password("password")
                                             .name("name")
                                             .nickname("nick")
                                             .birth("19941930")
                                             .build();

        memberJpaRepository.save(MemberEntity.of(joinRequest));

        ResultActions resultActions = mock.perform(
            post(PREFIX + "/join").contentType(MediaType.APPLICATION_JSON)
                                  .content(objectMapper.writeValueAsString(joinRequest))
        ).andDo(print());

        String contentAsString = resultActions.andReturn().getResponse()
                                              .getContentAsString(StandardCharsets.UTF_8);
        assertThat(contentAsString).contains("400");


    }

}
