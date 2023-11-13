package ai.serverapi.member.controller;

import ai.serverapi.global.base.Api;
import ai.serverapi.global.base.ResultCode;
import ai.serverapi.member.controller.request.JoinRequest;
import ai.serverapi.member.controller.request.LoginRequest;
import ai.serverapi.member.controller.response.JoinResponse;
import ai.serverapi.member.controller.response.LoginResponse;
import ai.serverapi.member.service.MemberAuthServiceImpl;
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

    private final MemberAuthServiceImpl memberAuthService;

    @PostMapping("/join")
    public ResponseEntity<Api<JoinResponse>> join(
        @RequestBody @Validated JoinRequest joinRequest,
        BindingResult bindingResult
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(
                                 new Api<>(
                                     ResultCode.POST.code,
                                     ResultCode.POST.message,
                                     memberAuthService.join(joinRequest))
                             );
    }

    @GetMapping("/hello")
    public ResponseEntity<Api<String>> hello() {
        return ResponseEntity.ok(
            new Api<>(
                ResultCode.SUCCESS.code,
                ResultCode.SUCCESS.message,
                "hello")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<Api<LoginResponse>> login(
        @RequestBody @Validated LoginRequest loginRequest,
        BindingResult bindingResult
    ) {
        return ResponseEntity.ok(
            new Api<>(
                ResultCode.SUCCESS.code,
                ResultCode.SUCCESS.message,
                memberAuthService.login(loginRequest))
        );
    }

    @GetMapping("/refresh/{refresh_token}")
    public ResponseEntity<Api<LoginResponse>> refresh(
        @PathVariable(value = "refresh_token") String refreshToken) {
        return ResponseEntity.ok(
            new Api<>(
                ResultCode.SUCCESS.code,
                ResultCode.SUCCESS.message,
                memberAuthService.refresh(refreshToken))
        );
    }
}