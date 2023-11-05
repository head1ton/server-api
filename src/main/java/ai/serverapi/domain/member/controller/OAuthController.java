package ai.serverapi.domain.member.controller;

import ai.serverapi.config.base.Api;
import ai.serverapi.config.base.ResultCode;
import ai.serverapi.domain.member.record.LoginRecord;
import ai.serverapi.domain.member.service.MemberAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api-prefix}/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final MemberAuthService memberAuthService;

    @GetMapping("/kakao")
    public ResponseEntity<Api<LoginRecord>> authKakao(@RequestParam("code") String code) {
        return ResponseEntity.ok(
            Api.<LoginRecord>builder()
               .code(ResultCode.SUCCESS.code)
               .message(ResultCode.SUCCESS.message)
               .data(memberAuthService.authKakao(code))
               .build()
        );
    }

    @GetMapping("/kakao/login")
    public ResponseEntity<Api<LoginRecord>> login(
        @RequestParam("access_token") String accessToken) {
        return ResponseEntity.ok(
            Api.<LoginRecord>builder()
               .code(ResultCode.SUCCESS.code)
               .message(ResultCode.SUCCESS.message)
               .data(memberAuthService.loginKakao(accessToken))
               .build()
        );
    }
}
