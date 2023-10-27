package ai.serverapi.controller.member;

import ai.serverapi.domain.dto.Api;
import ai.serverapi.domain.enums.ResultCode;
import ai.serverapi.domain.vo.MessageVo;
import ai.serverapi.domain.vo.member.MemberVo;
import ai.serverapi.service.member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api-prefix}/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("")
    public ResponseEntity<Api<MemberVo>> member(HttpServletRequest request) {
        return ResponseEntity.ok(
            Api.<MemberVo>builder()
               .code(ResultCode.SUCCESS.code)
               .message(ResultCode.SUCCESS.message)
               .data(memberService.member(request))
               .build()
        );
    }

    @PostMapping("/seller")
    public ResponseEntity<Api<MessageVo>> applySeller(HttpServletRequest request) {
        return ResponseEntity.ok(
            Api.<MessageVo>builder()
               .code(ResultCode.SUCCESS.code)
               .message(ResultCode.SUCCESS.message)
               .data(memberService.applySeller(request))
               .build()
        );
    }
}