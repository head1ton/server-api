package ai.serverapi.common.s3;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
public class S3ServiceTest {

    @Autowired
    private S3Service s3Service;

    @Test
    @Disabled
    @DisplayName("이미지 등록 성공")
    void putObjectSuccess() {
        List<MultipartFile> files = new LinkedList<>();
        files.add(new MockMultipartFile("test1", "test1.txt", StandardCharsets.UTF_8.name(),
            "1".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test2", "test2.txt", StandardCharsets.UTF_8.name(),
            "2".getBytes(StandardCharsets.UTF_8)));
        files.add(new MockMultipartFile("test3", "test3.txt", StandardCharsets.UTF_8.name(),
            "3".getBytes(StandardCharsets.UTF_8)));

        List<String> putObjectList = s3Service.putObject("house/test/", files);

        assertThat(putObjectList).isNotEmpty();
    }

}
