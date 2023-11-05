package ai.serverapi.domain.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.domain.common.record.UploadVo;
import ai.serverapi.domain.member.dto.LoginDto;
import ai.serverapi.domain.member.service.MemberAuthService;
import ai.serverapi.domain.member.vo.LoginVo;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
class CommonS3ServiceTest {

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    @Autowired
    private MemberAuthService memberAuthService;
    @Autowired
    private CommonS3Service commonS3Service;
    @Autowired
    private Environment env;

    @Test
    @Disabled("s3에 실제로 데이터가 올라가는 테스트")
    @DisplayName("이미 등록 성공")
    void uploadImage() {

        LoginDto loginDto = new LoginDto("seller@gmail.com", "password");
        LoginVo loginVo = memberAuthService.login(loginDto);
        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + loginVo.accessToken());

        List<MultipartFile> files = new LinkedList<>();
        String fileName1 = "test1.txt";
        String fileName2 = "test2.txt";
        String fileName3 = "test3.txt";

        files.add(new MockMultipartFile("test1", fileName1, StandardCharsets.UTF_8.name(),
            "abcd".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test2", fileName2, StandardCharsets.UTF_8.name(),
            "222".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test3", fileName3, StandardCharsets.UTF_8.name(),
            "3".getBytes(StandardCharsets.UTF_8)));

        UploadVo uploadVo = commonS3Service.s3UploadFile(files, "image/%s/%s/", request);

        assertThat(uploadVo.imageUrl()).contains(env.getProperty("cloud.s3.url"));

    }
}
