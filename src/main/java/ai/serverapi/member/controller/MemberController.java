package ai.serverapi.member.controller;

import ai.serverapi.global.base.Api;
import ai.serverapi.global.base.MessageVo;
import ai.serverapi.global.base.ResultCode;
import ai.serverapi.global.exception.DuringProcessException;
import ai.serverapi.member.controller.request.PatchMemberRequest;
import ai.serverapi.member.controller.request.PostIntroduceRequest;
import ai.serverapi.member.controller.request.PostRecipientRequest;
import ai.serverapi.member.controller.request.PostSellerRequest;
import ai.serverapi.member.controller.request.PutSellerRequest;
import ai.serverapi.member.controller.response.MemberResponse;
import ai.serverapi.member.controller.response.RecipientListResponse;
import ai.serverapi.member.service.MemberService;
import ai.serverapi.product.controller.response.SellerResponse;
import ai.serverapi.product.service.ProductService;
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
    private final ProductService productService;

    @GetMapping("")
    public ResponseEntity<Api<MemberResponse>> member(HttpServletRequest request) {
        return ResponseEntity.ok(
            new Api<>(
                ResultCode.SUCCESS.code,
                ResultCode.SUCCESS.message,
                memberService.member(request))
        );
    }

    @GetMapping("/seller")
    public ResponseEntity<Api<SellerResponse>> getSeller(HttpServletRequest request) {
        return ResponseEntity.ok(
            new Api<>(
                ResultCode.SUCCESS.code,
                ResultCode.SUCCESS.message,
                memberService.getSeller(request))
        );
    }

    @PostMapping("/seller")
    public ResponseEntity<Api<MessageVo>> postSeller(
        @RequestBody @Validated PostSellerRequest postSellerRequest,
        HttpServletRequest request,
        BindingResult bindingResult) {
        return ResponseEntity.ok(
            new Api<>(
                ResultCode.POST.code,
                ResultCode.POST.message,
                memberService.postSeller(postSellerRequest, request))
        );
    }

    @PutMapping("/seller")
    public ResponseEntity<Api<MessageVo>> putSeller(
        @RequestBody @Validated PutSellerRequest putSellerRequest,
        HttpServletRequest request,
        BindingResult bindingResult) {
        return ResponseEntity.ok(
            new Api<>(
                ResultCode.SUCCESS.code,
                ResultCode.SUCCESS.message,
                memberService.putSeller(putSellerRequest, request))
        );
    }

    @PatchMapping
    public ResponseEntity<Api<MessageVo>> patchMember(
        @RequestBody @Validated PatchMemberRequest patchMemberRequest,
        HttpServletRequest request,
        BindingResult bindingResult
    ) {
        return ResponseEntity.ok(
            new Api<>(
                ResultCode.SUCCESS.code,
                ResultCode.SUCCESS.message,
                memberService.patchMember(patchMemberRequest, request))
        );
    }

    @PostMapping("/recipient")
    public ResponseEntity<Api<MessageVo>> postRecipient(
        @RequestBody @Validated PostRecipientRequest postRecipientRequest,
        HttpServletRequest request,
        BindingResult bindingResult
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(
                                 new Api<>(
                                     ResultCode.POST.code,
                                     ResultCode.POST.message,
                                     memberService.postRecipient(postRecipientRequest,
                                          request))
                             );
    }

    @GetMapping("/recipient")
    public ResponseEntity<Api<RecipientListResponse>> getRecipient(HttpServletRequest request) {
        return ResponseEntity.ok(
            new Api<>(
                ResultCode.SUCCESS.code,
                ResultCode.SUCCESS.message,
                memberService.getRecipient(request))
        );
    }

    @PostMapping("/seller/introduce")
    public ResponseEntity<Api<MessageVo>> postIntroduce(
        @RequestBody @Validated PostIntroduceRequest postIntroduceRequest,
        HttpServletRequest request,
        BindingResult bindingResult
    ) {
        return ResponseEntity.ok(
            new Api<>(
                ResultCode.SUCCESS.code,
                ResultCode.SUCCESS.message,
                memberService.postIntroduce(postIntroduceRequest, request))
        );
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
            throw new DuringProcessException("소개 페이지 반환 실패", e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

        response.setStatus(200);
    }

}