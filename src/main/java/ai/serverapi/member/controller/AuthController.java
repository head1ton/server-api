package ai.serverapi.member.controller;

import ai.serverapi.config.base.Api;
import ai.serverapi.config.base.ResultCode;
import ai.serverapi.member.domain.dto.JoinDto;
import ai.serverapi.member.domain.dto.LoginDto;
import ai.serverapi.member.domain.vo.JoinVo;
import ai.serverapi.member.domain.vo.LoginVo;
import ai.serverapi.member.service.MemberAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api-prefix}/auth")
public class AuthController {

    private final MemberAuthService memberAuthService;

    @PostMapping("/join")
    public ResponseEntity<Api<JoinVo>> join(
        @RequestBody @Validated JoinDto joinDto,
        BindingResult bindingResult
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(Api.<JoinVo>builder()
                                      .code(ResultCode.POST.code)
                                      .message(ResultCode.POST.message)
                                      .data(memberAuthService.join(joinDto))
                                      .build());
    }

    @GetMapping("/hello")
    public ResponseEntity<Api<String>> hello() {
        return ResponseEntity.ok(Api.<String>builder()
                                    .code(ResultCode.SUCCESS.code)
                                    .message(ResultCode.SUCCESS.message)
                                    .data("hello")
                                    .build());
    }

    @PostMapping("/login")
    public ResponseEntity<Api<LoginVo>> login(
        @RequestBody @Validated LoginDto loginDto,
        BindingResult bindingResult
    ) {
        return ResponseEntity.ok(
            Api.<LoginVo>builder()
               .code(ResultCode.SUCCESS.code)
               .message(ResultCode.SUCCESS.message)
               .data(memberAuthService.login(loginDto))
               .build()
        );
    }

    @GetMapping("/refresh/{refresh_token}")
    public ResponseEntity<Api<LoginVo>> refresh(
        @PathVariable(value = "refresh_token") String refreshToken) {
        return ResponseEntity.ok(
            Api.<LoginVo>builder()
               .code(ResultCode.SUCCESS.code)
               .message(ResultCode.SUCCESS.message)
               .data(memberAuthService.refresh(refreshToken))
               .build()
        );
    }
}