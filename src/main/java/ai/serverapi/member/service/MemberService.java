package ai.serverapi.member.service;

import ai.serverapi.global.base.MessageVo;
import ai.serverapi.member.dto.request.PatchMemberRequest;
import ai.serverapi.member.dto.request.PostIntroduceRequest;
import ai.serverapi.member.dto.request.PostRecipientRequest;
import ai.serverapi.member.dto.request.PostSellerRequest;
import ai.serverapi.member.dto.request.PutSellerRequest;
import ai.serverapi.member.dto.response.MemberResponse;
import ai.serverapi.member.dto.response.RecipientListResponse;
import ai.serverapi.product.dto.response.SellerResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface MemberService {

    MemberResponse member(final HttpServletRequest request);

    MessageVo patchMember(
        final PatchMemberRequest patchMemberRequest,
        final HttpServletRequest request);

    MessageVo postRecipient(final PostRecipientRequest postRecipientRequest,
        final HttpServletRequest request);

    RecipientListResponse getRecipient(final HttpServletRequest request);

    MessageVo postSeller(PostSellerRequest postSellerRequest, HttpServletRequest request);

    MessageVo putSeller(PutSellerRequest putSellerRequest, HttpServletRequest request);

    SellerResponse getSeller(HttpServletRequest request);

    MessageVo postIntroduce(PostIntroduceRequest postIntroduceRequest,
        HttpServletRequest request);

    String getIntroduce(HttpServletRequest request);

    String getIntroduce(Long sellerId);
}
