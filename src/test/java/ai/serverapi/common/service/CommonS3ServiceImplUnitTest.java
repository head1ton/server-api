package ai.serverapi.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import ai.serverapi.common.dto.response.UploadResponse;
import ai.serverapi.global.s3.S3Service;
import ai.serverapi.global.security.TokenProvider;
import ai.serverapi.member.domain.entity.MemberEntity;
import ai.serverapi.member.enums.MemberRole;
import ai.serverapi.member.repository.MemberJpaRepository;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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
class CommonS3ServiceImplUnitTest {

    @InjectMocks
    private CommonS3ServiceImpl commonS3Service;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private MemberJpaRepository memberJpaRepository;
    @Mock
    private Environment env;
    @Mock
    private S3Service s3Service;

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final List<MultipartFile> files = new LinkedList<>();
    private final String fileName1 = "test1.txt";
    private final String fileName2 = "test2.txt";
    private final String fileName3 = "test3.txt";
    private final String s3Url = "https://s3.aws.url";

    @BeforeEach
    void setUp() {
        files.clear();

        files.add(new MockMultipartFile("test1", fileName1, StandardCharsets.UTF_8.name(),
            "abcd".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test2", fileName2, StandardCharsets.UTF_8.name(),
            "222".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test3", fileName3, StandardCharsets.UTF_8.name(),
            "3".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    @DisplayName("upload image 실패")
    void uploadImageFail1() {

        Throwable throwable = catchThrowable(
            () -> commonS3Service.s3UploadFile(files, "image/%s/%s/", request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("upload image 성공")
    void uploadImageSuccess1() {

        LocalDateTime now = LocalDateTime.now();
        MemberEntity memberEntity = new MemberEntity(1L, "email@gmail.com", "password", "nickname",
            "name",
            "19941030", MemberRole.SELLER, null, null, now, now);

        BDDMockito.given(memberJpaRepository.findById(any())).willReturn(Optional.of(memberEntity));
        BDDMockito.given(env.getProperty(anyString())).willReturn(s3Url);

        List<String> list = new LinkedList<>();
        list.add(fileName1);
        list.add(fileName2);
        list.add(fileName3);
        BDDMockito.given(s3Service.putObject(anyString(), anyString(), any())).willReturn(list);

        UploadResponse uploadResponse = commonS3Service.s3UploadFile(files, "image/%s/%s/",
            request);

        assertThat(uploadResponse.getUrl()).contains(s3Url);
    }

    @Test
    @DisplayName("uploadImage")
    void uploadImage() {

        // 토큰 받아오고
//        BDDMockito.given(tokenProvider.resolveToken(any())).willReturn("token");
        LocalDateTime now = LocalDateTime.now();
        // 토큰으로 회원인지 확인하고
        MemberEntity memberEntity = new MemberEntity(1L, "email@gmail.com", "password", "nickname",
            "name",
            "19941030", MemberRole.SELLER, null, null, now, now);
        BDDMockito.given(memberJpaRepository.findById(any())).willReturn(Optional.of(memberEntity));
        // 그런다음 이미지 업로드 주소 받아오고
        BDDMockito.given(env.getProperty(anyString())).willReturn(s3Url);

        List<String> list = new LinkedList<>();
        list.add(fileName1);
        list.add(fileName2);
        list.add(fileName3);

//        // 이미지 업로드 하기 위해 파일 체크해서 파일 리스트 받아오고
        BDDMockito.given(s3Service.putObject(anyString(), anyString(), any())).willReturn(list);

        // 이미지 업로드
        UploadResponse uploadResponse = commonS3Service.s3UploadFile(files, "html/%s/%s", request);

        assertThat(uploadResponse.getUrl()).contains(s3Url);

    }
}
