package ai.serverapi.controller.member;

import ai.serverapi.domain.dto.Api;
import ai.serverapi.domain.dto.member.JoinDto;
import ai.serverapi.domain.dto.member.LoginDto;
import ai.serverapi.domain.enums.ResultCode;
import ai.serverapi.domain.vo.member.JoinVo;
import ai.serverapi.domain.vo.member.LoginVo;
import ai.serverapi.domain.vo.member.MemberVo;
import ai.serverapi.service.member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<Api<JoinVo>> join(
        @RequestBody @Validated JoinDto joinDto,
        BindingResult bindingResult
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(Api.<JoinVo>builder()
                                      .code(ResultCode.POST.CODE)
                                      .message(ResultCode.POST.MESSAGE)
                                      .data(memberService.join(joinDto))
                                      .build());
    }

    @GetMapping("/hello")
    public ResponseEntity<Api<String>> hello() {
        return ResponseEntity.ok(Api.<String>builder()
                                    .code(ResultCode.SUCCESS.CODE)
                                    .message(ResultCode.SUCCESS.MESSAGE)
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
               .code(ResultCode.SUCCESS.CODE)
               .message(ResultCode.SUCCESS.MESSAGE)
               .data(memberService.login(loginDto))
               .build()
        );
    }

    @GetMapping("/refresh/{refresh_token}")
    public ResponseEntity<Api<LoginVo>> refresh(
        @PathVariable(value = "refresh_token") String refreshToken) {
        return ResponseEntity.ok(
            Api.<LoginVo>builder()
               .code(ResultCode.SUCCESS.CODE)
               .message(ResultCode.SUCCESS.MESSAGE)
               .data(memberService.refresh(refreshToken))
               .build()
        );
    }

    @GetMapping("")
    public ResponseEntity<Api<MemberVo>> member(HttpServletRequest request) {
        return ResponseEntity.ok(
            Api.<MemberVo>builder()
               .code(ResultCode.SUCCESS.CODE)
               .message(ResultCode.SUCCESS.MESSAGE)
               .data(memberService.member(request))
               .build()
        );
    }
}