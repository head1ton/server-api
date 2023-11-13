package ai.serverapi.common.service;

import ai.serverapi.common.dto.response.UploadResponse;
import ai.serverapi.global.s3.S3Service;
import ai.serverapi.global.security.TokenProvider;
import ai.serverapi.member.repository.MemberJpaRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CommonS3ServiceImpl implements CommonS3Service {

    private final TokenProvider tokenProvider;
    private final MemberJpaRepository memberJpaRepository;
    private final Environment env;
    private final S3Service s3Service;

    @Override
    public UploadResponse s3UploadFile(final List<MultipartFile> files, String pathFormat,
        final HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);

        memberJpaRepository.findById(memberId).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 회원입니다.")
        );

        String s3Url = env.getProperty("cloud.s3.url");

        List<String> putFileUrlList = makeFileUrlList(files, memberId, pathFormat);

        return new UploadResponse(String.format("%s/%s", s3Url,
            Optional.ofNullable(putFileUrlList.get(0)).orElse("")));
    }

    private List<String> makeFileUrlList(final List<MultipartFile> files, final Long memberId,
        String pathFormat) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter pathFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");
        String path = now.format(pathFormatter);
        String fileName = now.format(timeFormatter);

        return s3Service.putObject(
            String.format(pathFormat, memberId, path),
            fileName, files);
    }
}
