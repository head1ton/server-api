package ai.serverapi.member.service;

import ai.serverapi.member.dto.request.JoinRequest;
import ai.serverapi.member.dto.response.JoinResponse;
import ai.serverapi.member.dto.response.LoginResponse;


public interface MemberAuthService {

    JoinResponse join(JoinRequest joinRequest);

    LoginResponse refresh(String refreshToken);

    LoginResponse authKakao(String code);

    LoginResponse loginKakao(String accessToken);
}
