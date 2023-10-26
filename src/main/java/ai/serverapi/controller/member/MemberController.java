package ai.serverapi.controller.member;

import ai.serverapi.domain.dto.Api;
import ai.serverapi.domain.dto.member.JoinDto;
import ai.serverapi.domain.enums.ResultCode;
import ai.serverapi.domain.vo.member.JoinVo;
import ai.serverapi.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
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
}
