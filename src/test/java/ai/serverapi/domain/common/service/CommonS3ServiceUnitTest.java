package ai.serverapi.domain.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import ai.serverapi.config.s3.S3Service;
import ai.serverapi.config.security.TokenProvider;
import ai.serverapi.domain.common.record.UploadRecord;
import ai.serverapi.domain.member.entity.Member;
import ai.serverapi.domain.member.enums.Role;
import ai.serverapi.domain.member.repository.MemberRepository;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class CommonS3ServiceUnitTest {

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    @InjectMocks
    private CommonS3Service commonS3Service;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private Environment env;
    @Mock
    private S3Service s3Service;

    @Test
    @DisplayName("uploadImage")
    void uploadImage() {
        // 파일 만들고
        List<MultipartFile> files = new LinkedList<>();
        String fileName1 = "test1.txt";
        String fileName2 = "test2.txt";
        String fileName3 = "test3.txt";
        String s3Url = "https://s3.aws.url";

        files.add(new MockMultipartFile("test1", fileName1, StandardCharsets.UTF_8.name(),
            "abcd".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test2", fileName1, StandardCharsets.UTF_8.name(),
            "222".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test3", fileName1, StandardCharsets.UTF_8.name(),
            "3".getBytes(StandardCharsets.UTF_8)));

        // 토큰 받아오고
//        BDDMockito.given(tokenProvider.resolveToken(any())).willReturn("token");
        LocalDateTime now = LocalDateTime.now();
        // 토큰으로 회원인지 확인하고
        Member member = new Member(1L, "email@gmail.com", "password", "nickname", "name",
            "19941030", Role.SELLER, null, null, now, now);
        BDDMockito.given(memberRepository.findById(any())).willReturn(Optional.of(member));
        // 그런다음 이미지 업로드 주소 받아오고
        BDDMockito.given(env.getProperty(anyString())).willReturn(s3Url);

        List<String> list = new LinkedList<>();
        list.add(fileName1);
        list.add(fileName2);
        list.add(fileName3);

//        // 이미지 업로드 하기 위해 파일 체크해서 파일 리스트 받아오고
        BDDMockito.given(s3Service.putObject(anyString(), anyString(), any())).willReturn(list);

        // 이미지 업로드
        UploadRecord uploadRecord = commonS3Service.uploadImage(files, request);

        assertThat(uploadRecord.imageUrl()).contains(s3Url);

    }
}
