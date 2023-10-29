package ai.serverapi.service.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.domain.dto.member.LoginDto;
import ai.serverapi.domain.vo.common.UploadVo;
import ai.serverapi.domain.vo.member.LoginVo;
import ai.serverapi.service.member.MemberAuthService;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
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
    @DisplayName("uploadImage")
    void uploadImage() {

        LoginDto loginDto = new LoginDto("seller@gmail.com", "password");
        LoginVo loginVo = memberAuthService.login(loginDto);

        request.addHeader(AUTHORIZATION, "Bearer " + loginVo.getAccessToken());

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

        UploadVo uploadVo = commonS3Service.uploadImage(files, request);

        assertThat(uploadVo.getImageUrl()).contains(env.getProperty("cloud.s3.url"));

    }
}
