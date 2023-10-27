package ai.serverapi.controller.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import ai.serverapi.BaseTest;
import ai.serverapi.domain.dto.member.JoinDto;
import ai.serverapi.domain.entity.member.Member;
import ai.serverapi.repository.member.MemberRepository;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
public class AuthControllerTest extends BaseTest {

    @Autowired
    private MemberRepository memberRepository;
    private final String PREFIX = "/api/auth";

    @Test
    @DisplayName("중복 회원 가입 실패")
    public void joinFail() throws Exception {
        JoinDto joinDto = new JoinDto("venus@mail.com", "password", "name", "nick", "19941930");

        memberRepository.save(Member.of(joinDto));

        ResultActions resultActions = mockMvc.perform(
            post(PREFIX + "/join").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(joinDto))
        ).andDo(print());

        String contentAsString = resultActions.andReturn().getResponse()
                                              .getContentAsString(StandardCharsets.UTF_8);
        assertThat(contentAsString).contains("400");


    }

}
