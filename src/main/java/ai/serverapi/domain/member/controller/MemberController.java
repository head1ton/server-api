package ai.serverapi.domain.member.controller;

import ai.serverapi.config.base.Api;
import ai.serverapi.config.base.MessageVo;
import ai.serverapi.config.base.ResultCode;
import ai.serverapi.domain.member.dto.PatchMemberDto;
import ai.serverapi.domain.member.dto.PostBuyerDto;
import ai.serverapi.domain.member.dto.PostRecipientDto;
import ai.serverapi.domain.member.dto.PutBuyerDto;
import ai.serverapi.domain.member.service.MemberService;
import ai.serverapi.domain.member.vo.BuyerVo;
import ai.serverapi.domain.member.vo.MemberVo;
import ai.serverapi.domain.member.vo.RecipientListVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/buyer")
    public ResponseEntity<Api<MessageVo>> postBuyer(
        @RequestBody @Validated PostBuyerDto postBuyerDto,
        HttpServletRequest request,
        BindingResult bindingResult
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(Api.<MessageVo>builder()
                                      .code(ResultCode.POST.code)
                                      .message(ResultCode.POST.message)
                                      .data(memberService.postBuyer(postBuyerDto, request))
                                      .build()
                             );
    }

    @GetMapping("/buyer")
    public ResponseEntity<Api<BuyerVo>> getBuyer(HttpServletRequest request) {
        return ResponseEntity.ok(Api.<BuyerVo>builder()
                                    .code(ResultCode.SUCCESS.code)
                                    .message(ResultCode.SUCCESS.message)
                                    .data(memberService.getBuyer(request))
                                    .build());
    }

    @PutMapping("/buyer")
    public ResponseEntity<Api<MessageVo>> putBuyer(
        @RequestBody @Validated PutBuyerDto putBuyerDto,
        BindingResult bindingResult) {
        return ResponseEntity.ok(Api.<MessageVo>builder()
                                    .code(ResultCode.SUCCESS.code)
                                    .message(ResultCode.SUCCESS.message)
                                    .data(memberService.putBuyer(putBuyerDto))
                                    .build());
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
    public ResponseEntity<Api<RecipientListVo>> getRecipient(HttpServletRequest request) {
        return ResponseEntity.ok(Api.<RecipientListVo>builder()
                                    .code(ResultCode.SUCCESS.code)
                                    .message(ResultCode.SUCCESS.message)
                                    .data(memberService.getRecipient(request))
                                    .build());
    }
}