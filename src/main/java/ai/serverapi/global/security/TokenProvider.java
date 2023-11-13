package ai.serverapi.global.security;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.member.controller.response.LoginResponse;
import com.github.dockerjava.api.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 30;    // 30 minutes
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7;  // 7 days
    private final Key key;
    private static final String TYPE = "Bearer ";

    @Value("${spring.profiles.active}")
    private String profile;

    public TokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    public LoginResponse generateTokenDto(final Authentication authenticate) {
        // 권한 가져오고
        String authorities = authenticate.getAuthorities().stream()
                                         .map(GrantedAuthority::getAuthority)
                                         .collect(Collectors.joining(","));

        // 현재 시간
        long now = (new Date()).getTime();

        // AccessToken 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = createAccessToken(authenticate.getName(), authorities,
            accessTokenExpiresIn);

        // RefreshToken 생성
        String refreshToken = Jwts.builder()
                                  .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                                  .signWith(key, SignatureAlgorithm.HS512)
                                  .compact();

        return LoginResponse.builder()
                            .type(TYPE)
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .accessTokenExpired(accessTokenExpiresIn.getTime())
                            .build();
    }

    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new UnauthorizedException("권한 정보가 없는 토큰입니다.");
        }

        // Claim 에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(
                                                                       claims.get(AUTHORITIES_KEY).toString().split(","))
                                                                   .map(SimpleGrantedAuthority::new)
                                                                   .toList();

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.debug("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.debug("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.debug("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.debug("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public Claims parseClaims(final String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
                       .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String createAccessToken(
        final String sub,
        final String authorities,
        final Date accessTokenExpiredIn) {
        return Jwts.builder()
                   .setSubject(
                       sub)                                                             // payload "sub" : "name"
                   .claim(AUTHORITIES_KEY, authorities)     // payload "auth" : "ROLE_USER"
                   .setExpiration(accessTokenExpiredIn)     // payload "exp" : 1516239022
                   .signWith(key, SignatureAlgorithm.HS512)     // header "alg" : "HS512"
                   .compact();
    }

    private static Long getTestMemberId(final String token) {
        Map<String, Long> memberMap = new HashMap<>();
        memberMap.put("member-test-token", 1L);
        memberMap.put("seller-test-token", 2L);
        memberMap.put("seller2-test-token", 3L);

        return memberMap.get(token);
    }

    public Long getMemberId(final String token) {
        if (profile.equals("local") && token.contains("test")) {
            return getTestMemberId(token);
        }
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.get("sub").toString());
    }

    public Long getMemberId(HttpServletRequest request) {
        String token = resolveToken(request);

        if (profile.equals("local") && token.contains("test")) {
            return getTestMemberId(token);
        }
        return getMemberId(token);
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TYPE)) {
            return bearerToken.substring(TYPE.length());
        }
        return null;
    }
}
