package ai.serverapi.controller.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import ai.serverapi.BaseTest;
import ai.serverapi.domain.dto.member.JoinDto;
import ai.serverapi.domain.enums.ResultCode;
import ai.serverapi.repository.MemberRepository;
import ai.serverapi.service.member.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
public class MemberControllerTest extends BaseTest {

    @Test
    @DisplayName("회원 가입 성공")
    public void join() throws Exception {
        JoinDto joinDto = new JoinDto("tester@mail.com", "password", "name", "nick", "19941930");

        ResultActions resultActions = mockMvc.perform(
            post("/member/join").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(joinDto))
        ).andDo(print());

        String contentAsString = resultActions.andReturn().getResponse()
                                              .getContentAsString(StandardCharsets.UTF_8);
        assertThat(contentAsString).contains(ResultCode.POST.CODE);


    }

}
