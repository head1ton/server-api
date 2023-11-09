package ai.serverapi.global.s3;

import ai.serverapi.global.exception.DuringProcessException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class S3Service {

    private final S3Client s3Client;
    private final Environment env;

    @Value("${cloud.s3.bucket}")
    private String bucketName;
    @Value("${cloud.s3.url}")
    private String url;

    @Transactional
    public List<String> putObject(final String path, String fileName,
        final List<MultipartFile> files) {
        List<String> list = new LinkedList<>();
        int count = 0;

        for (MultipartFile file : files) {
            String originalFilename = Optional.ofNullable(file.getOriginalFilename()).orElse("");
            String fileExtension = originalFilename.substring(originalFilename.indexOf('.'));

            String contentType = file.getContentType();
            String makeFileName = String.format("%s%s_%s%s", path, fileName, count, fileExtension);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                                                .bucket(bucketName)
                                                                .key(makeFileName)
                                                                .contentType(contentType)
                                                                .contentLength(file.getSize())
                                                                .build();

            PutObjectResponse response;
            try {
                response = s3Client.putObject(putObjectRequest,
                    RequestBody.fromBytes(file.getBytes()));
            } catch (IOException ie) {
                log.error("파일을 읽어드이는데 에러가 발생했습니다.");
                log.error(ie.getMessage());
                throw new IllegalStateException(ie.getMessage(), ie);
            }

            if (response.sdkHttpResponse().statusText().orElse("FAIL").equals("OK")) {
                list.add(makeFileName);
            } else {
                throw new IllegalStateException("AWS에 파일을 올리는데 실패했습니다.");
            }
            count++;
        }
        return list;
    }

    public String getObject(String path, String bucketName) {
        try {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                                                             .key(path)
                                                             .bucket(bucketName)
                                                             .build();

            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(objectRequest);
            String text = new String(response.asByteArray(), StandardCharsets.UTF_8);
            log.debug("파일 내용 : " + text);

            return new String(response.asByteArray(), StandardCharsets.UTF_8);
        } catch (S3Exception ae) {
            log.error("AWS 와 통신에 문제가 발생했습니다.");
            log.error(ae.getMessage());
            throw new DuringProcessException("AWS와 통신에 문제가 발생했습니다.", ae);
        }
    }
}
