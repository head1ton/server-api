package ai.serverapi.member.controller;

import ai.serverapi.global.base.Api;
import ai.serverapi.global.base.ResultCode;
import ai.serverapi.member.dto.request.JoinRequest;
import ai.serverapi.member.dto.request.LoginRequest;
import ai.serverapi.member.dto.response.JoinResponse;
import ai.serverapi.member.dto.response.LoginResponse;
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
    public ResponseEntity<Api<JoinResponse>> join(
        @RequestBody @Validated JoinRequest joinRequest,
        BindingResult bindingResult
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(Api.<JoinResponse>builder()
                                      .code(ResultCode.POST.code)
                                      .message(ResultCode.POST.message)
                                      .data(memberAuthService.join(joinRequest))
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
    public ResponseEntity<Api<LoginResponse>> login(
        @RequestBody @Validated LoginRequest loginRequest,
        BindingResult bindingResult
    ) {
        return ResponseEntity.ok(
            Api.<LoginResponse>builder()
               .code(ResultCode.SUCCESS.code)
               .message(ResultCode.SUCCESS.message)
               .data(memberAuthService.login(loginRequest))
               .build()
        );
    }

    @GetMapping("/refresh/{refresh_token}")
    public ResponseEntity<Api<LoginResponse>> refresh(
        @PathVariable(value = "refresh_token") String refreshToken) {
        return ResponseEntity.ok(
            Api.<LoginResponse>builder()
               .code(ResultCode.SUCCESS.code)
               .message(ResultCode.SUCCESS.message)
               .data(memberAuthService.refresh(refreshToken))
               .build()
        );
    }
}