package ai.serverapi.common.controller;

import ai.serverapi.common.domain.vo.UploadVo;
import ai.serverapi.common.service.CommonS3Service;
import ai.serverapi.global.base.Api;
import ai.serverapi.global.base.ResultCode;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api-prefix}/common")
public class CommonController {

    private final CommonS3Service commonS3Service;

    @PostMapping("/image")
    public ResponseEntity<Api<UploadVo>> uploadImage(
        @RequestPart List<MultipartFile> image,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.SC_CREATED)
                             .body(Api.<UploadVo>builder()
                                      .code(ResultCode.SUCCESS.code)
                                      .message(ResultCode.SUCCESS.message)
                                      .data(commonS3Service.s3UploadFile(image, "image/%s/%s/",
                                          request))
                                      .build());
    }

    @PostMapping("/html")
    public ResponseEntity<Api<UploadVo>> uploadHtml(
        @RequestPart List<MultipartFile> html,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.SC_CREATED)
                             .body(Api.<UploadVo>builder()
                                      .code(ResultCode.SUCCESS.code)
                                      .message(ResultCode.SUCCESS.message)
                                      .data(commonS3Service.s3UploadFile(html, "html/%s/%s/",
                                          request))
                                      .build());
    }
}
