package ai.serverapi.domain.member.controller;

import ai.serverapi.config.base.Api;
import ai.serverapi.config.base.MessageVo;
import ai.serverapi.config.base.ResultCode;
import ai.serverapi.domain.member.dto.PatchMemberDto;
import ai.serverapi.domain.member.dto.PostRecipientDto;
import ai.serverapi.domain.member.record.MemberRecord;
import ai.serverapi.domain.member.record.RecipientListRecord;
import ai.serverapi.domain.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api-prefix}/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("")
    public ResponseEntity<Api<MemberRecord>> member(HttpServletRequest request) {
        return ResponseEntity.ok(
            Api.<MemberRecord>builder()
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

    @PatchMapping
    public ResponseEntity<Api<MessageVo>> patchMember(
        @RequestBody @Validated PatchMemberDto patchMemberDto,
        HttpServletRequest request,
        BindingResult bindingResult
    ) {
        return ResponseEntity.ok(
            Api.<MessageVo>builder()
               .code(ResultCode.SUCCESS.code)
               .message(ResultCode.SUCCESS.message)
               .data(memberService.patchMember(patchMemberDto, request))
               .build()
        );
    }

    @PostMapping("/recipient")
    public ResponseEntity<Api<MessageVo>> postRecipient(
        @RequestBody @Validated PostRecipientDto postRecipientDto,
        HttpServletRequest request,
        BindingResult bindingResult
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(Api.<MessageVo>builder()
                                      .code(ResultCode.POST.code)
                                      .message(ResultCode.POST.message)
                                      .data(memberService.postRecipient(postRecipientDto,
                                          request))
                                      .build());
    }

    @GetMapping("/recipient")
    public ResponseEntity<Api<RecipientListRecord>> getRecipient(HttpServletRequest request) {
        return ResponseEntity.ok(Api.<RecipientListRecord>builder()
                                    .code(ResultCode.SUCCESS.code)
                                    .message(ResultCode.SUCCESS.message)
                                    .data(memberService.getRecipient(request))
                                    .build());
    }
}