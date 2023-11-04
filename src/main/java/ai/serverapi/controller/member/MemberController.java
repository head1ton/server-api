package ai.serverapi.controller.member;

import ai.serverapi.domain.dto.Api;
import ai.serverapi.domain.dto.member.PatchMemberDto;
import ai.serverapi.domain.dto.member.PostBuyerInfoDto;
import ai.serverapi.domain.dto.member.PostRecipientInfo;
import ai.serverapi.domain.dto.member.PutBuyerInfoDto;
import ai.serverapi.domain.enums.ResultCode;
import ai.serverapi.domain.vo.MessageVo;
import ai.serverapi.domain.vo.member.BuyerInfoVo;
import ai.serverapi.domain.vo.member.MemberVo;
import ai.serverapi.service.member.MemberService;
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

    @PostMapping("/buyer-info")
    public ResponseEntity<Api<MessageVo>> postBuyerInfo(
        @RequestBody @Validated PostBuyerInfoDto postBuyerInfoDto,
        HttpServletRequest request,
        BindingResult bindingResult
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(Api.<MessageVo>builder()
                                      .code(ResultCode.POST.code)
                                      .message(ResultCode.POST.message)
                                      .data(memberService.postBuyerInfo(postBuyerInfoDto, request))
                                      .build()
                             );
    }

    @GetMapping("/buyer-info")
    public ResponseEntity<Api<BuyerInfoVo>> getBuyerInfo(HttpServletRequest request) {
        return ResponseEntity.ok(Api.<BuyerInfoVo>builder()
                                    .code(ResultCode.SUCCESS.code)
                                    .message(ResultCode.SUCCESS.message)
                                    .data(memberService.getBuyerInfo(request))
                                    .build());
    }

    @PutMapping("/buyer-info")
    public ResponseEntity<Api<MessageVo>> putBuyerInfo(
        @RequestBody @Validated PutBuyerInfoDto putBuyerInfoDto,
        BindingResult bindingResult) {
        return ResponseEntity.ok(Api.<MessageVo>builder()
                                    .code(ResultCode.SUCCESS.code)
                                    .message(ResultCode.SUCCESS.message)
                                    .data(memberService.putBuyerInfo(putBuyerInfoDto))
                                    .build());
    }

    @PostMapping("/recipient")
    public ResponseEntity<Api<MessageVo>> postRecipientInfo(
        @RequestBody @Validated PostRecipientInfo postRecipientInfo,
        HttpServletRequest request,
        BindingResult bindingResult
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(Api.<MessageVo>builder()
                                      .code(ResultCode.POST.code)
                                      .message(ResultCode.POST.message)
                                      .data(memberService.postRecipientInfo(postRecipientInfo,
                                          request))
                                      .build());
    }
}