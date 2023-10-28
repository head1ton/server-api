package ai.serverapi.common.s3;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

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
        int count = 1;
        long size = Long.parseLong(env.getProperty("cloud.s3.size"));

        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            assert originalFilename != null;
            String fileExtension = originalFilename.substring(originalFilename.indexOf('.'));
            long fileSize = file.getSize();
            String contentType = file.getContentType();
            String makeFileName = String.format("%s%s_%s%s", path, fileName, count, fileExtension);
            if (fileSize > size) {
                throw new IllegalArgumentException(
                    String.format("file size가 너무 큽니다. 최대 사이즈 : %s, %s번째 파일", size, count));
            }

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
                throw new RuntimeException(ie.getMessage());
            }

            if (response.sdkHttpResponse().statusText().orElse("FAIL").equals("OK")) {
                list.add(makeFileName);
            } else {
                throw new RuntimeException("AWS에 파일을 올리는데 실패했습니다.");
            }
            count++;
        }
        return list;
    }
}
