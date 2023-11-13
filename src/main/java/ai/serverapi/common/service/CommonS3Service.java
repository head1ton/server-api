package ai.serverapi.common.service;

import ai.serverapi.common.dto.response.UploadResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;


public interface CommonS3Service {

    UploadResponse s3UploadFile(final List<MultipartFile> files, String pathFormat,
        final HttpServletRequest request);
}
