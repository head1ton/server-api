package ai.serverapi.controller;

import static ai.serverapi.domain.enums.ResultCode.SUCCESS;

import ai.serverapi.domain.dto.Api;
import ai.serverapi.domain.vo.MessageVo;
import ai.serverapi.service.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class HelloController {

    private final HelloService helloService;

    @GetMapping("/hello")
    public ResponseEntity<Api<MessageVo>> hello() {
        return ResponseEntity.ok(Api.<MessageVo>builder()
                                    .code(SUCCESS.CODE)
                                    .message(SUCCESS.MESSAGE)
                                    .data(helloService.hello())
                                    .build());
    }


}
