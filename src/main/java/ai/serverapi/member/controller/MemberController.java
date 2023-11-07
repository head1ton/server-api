package ai.serverapi.member.controller;

import ai.serverapi.global.base.Api;
import ai.serverapi.global.base.MessageVo;
import ai.serverapi.global.base.ResultCode;
import ai.serverapi.member.domain.dto.PatchMemberDto;
import ai.serverapi.member.domain.dto.PostIntroduceDto;
import ai.serverapi.member.domain.dto.PostRecipientDto;
import ai.serverapi.member.domain.dto.PostSellerDto;
import ai.serverapi.member.domain.dto.PutSellerDto;
import ai.serverapi.member.domain.vo.MemberVo;
import ai.serverapi.member.domain.vo.RecipientListVo;
import ai.serverapi.member.service.MemberService;
import ai.serverapi.product.domain.vo.SellerVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
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

    @GetMapping("/seller")
    public ResponseEntity<Api<SellerVo>> getSeller(HttpServletRequest request) {
        return ResponseEntity.ok(
            Api.<SellerVo>builder()
               .code(ResultCode.SUCCESS.code)
               .message(ResultCode.SUCCESS.message)
               .data(memberService.getSeller(request))
               .build()
        );
    }

    @PostMapping("/seller")
    public ResponseEntity<Api<MessageVo>> postSeller(
        @RequestBody @Validated PostSellerDto postSellerDto,
        HttpServletRequest request,
        BindingResult bindingResult) {
        return ResponseEntity.ok(
            Api.<MessageVo>builder()
               .code(ResultCode.POST.code)
               .message(ResultCode.POST.message)
               .data(memberService.postSeller(postSellerDto, request))
               .build()
        );
    }

    @PutMapping("/seller")
    public ResponseEntity<Api<MessageVo>> putSeller(
        @RequestBody @Validated PutSellerDto putSellerDto,
        HttpServletRequest request,
        BindingResult bindingResult) {
        return ResponseEntity.ok(
            Api.<MessageVo>builder()
               .code(ResultCode.SUCCESS.code)
               .message(ResultCode.SUCCESS.message)
               .data(memberService.putSeller(putSellerDto, request))
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
    public ResponseEntity<Api<RecipientListVo>> getRecipient(HttpServletRequest request) {
        return ResponseEntity.ok(Api.<RecipientListVo>builder()
                                    .code(ResultCode.SUCCESS.code)
                                    .message(ResultCode.SUCCESS.message)
                                    .data(memberService.getRecipient(request))
                                    .build());
    }

    @PostMapping("/seller/introduce")
    public ResponseEntity<Api<MessageVo>> postIntroduce(
        @RequestBody @Validated PostIntroduceDto postIntroduceDto,
        HttpServletRequest request,
        BindingResult bindingResult
    ) {
        return ResponseEntity.ok(Api.<MessageVo>builder()
                                    .code(ResultCode.SUCCESS.code)
                                    .message(ResultCode.SUCCESS.message)
                                    .data(memberService.postIntroduce(postIntroduceDto, request))
                                    .build());
    }

    @GetMapping("/seller/introduce")
    public void postIntroduce(HttpServletRequest request, HttpServletResponse response) {
        String introduce = memberService.getIntroduce(request);
        PrintWriter writer = null;
        try {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("text/html; charset=UTF-8");

            writer = response.getWriter();
            writer.print(introduce);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

        response.setStatus(200);
    }
}