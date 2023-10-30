package ai.serverapi.controller.member;

import ai.serverapi.domain.dto.Api;
import ai.serverapi.domain.enums.ResultCode;
import ai.serverapi.domain.vo.member.LoginVo;
import ai.serverapi.service.member.MemberAuthService;
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
    public ResponseEntity<Api<LoginVo>> login(@RequestParam("code") String code) {
        return ResponseEntity.ok(
            Api.<LoginVo>builder()
               .code(ResultCode.SUCCESS.code)
               .message(ResultCode.SUCCESS.message)
               .data(memberAuthService.loginKakao(code))
               .build()
        );
    }
}
