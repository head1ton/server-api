package ai.serverapi.controller;

import static ai.serverapi.domain.enums.ResultCode.SUCCESS;

import ai.serverapi.domain.dto.Api;
import ai.serverapi.domain.dto.TestDto;
import ai.serverapi.domain.vo.MessageVo;
import ai.serverapi.service.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping("/validation")
    public ResponseEntity<Api<MessageVo>> validation(
        @RequestBody @Validated TestDto testDto,
        BindingResult bindingResult
    ) {
        return ResponseEntity.ok(Api.<MessageVo>builder()
                                    .code(SUCCESS.CODE)
                                    .message(SUCCESS.MESSAGE)
                                    .data(helloService.hello())
                                    .build());
    }

    @GetMapping("/validation")
    public ResponseEntity<Api<MessageVo>> validation2(
        @RequestParam(name = "id") Long id) {
        return ResponseEntity.ok(Api.<MessageVo>builder()
                                    .code(SUCCESS.CODE)
                                    .message(SUCCESS.MESSAGE)
                                    .data(helloService.hello())
                                    .build());
    }


}
