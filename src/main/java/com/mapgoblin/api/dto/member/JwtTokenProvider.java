package com.mapgoblin.api.dto.member;

import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.base.MemberRole;
import com.mapgoblin.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    private String secretKey = "mapgoblin";

    /**
     * token valid time : 30min
     */
    private long tokenValidTime = 30 * 60 * 1000L;

    private final MemberService memberService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /**
     * Create JWT token
     *
     * @param userPk
     * @param role
     * @return
     */
    public String createToken(String userPk, MemberRole role) {
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("role", role);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * Authentication information extraction from token
     *
     * @param token
     * @return
     */
    public Authentication getAuthentication(String token) {

        Member member = memberService.findByUserId(this.getUserPk(token));

        return new UsernamePasswordAuthenticationToken(member, "");
    }

    /**
     * Member information extraction from token
     *
     * @param token
     * @return
     */
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Get token value from request header
     * "X-AUTH-TOKEN" : "Token value"
     *
     * @param request
     * @return
     */
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    /**
     * Token validation
     *
     * @param jwtToken
     * @return
     */
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
