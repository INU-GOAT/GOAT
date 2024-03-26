package com.capstone.goat.config;


import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import com.capstone.goat.dto.request.TokenDto;
import com.capstone.goat.exception.ex.CustomErrorCode;
import com.capstone.goat.exception.ex.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenProvider {

    private final UserDetailsService userDetailsService;
    @Value("${jwtSecret}")
    private String secret;
    @Value("${refreshSecret}")
    private String refreshSecret;
    private Key secretKey;
    private Key refreshKey;
    private final long tokenValidMillisecond = 1000L * 60 * 15 ;//15분
    private final long refreshValidMillisecond = 1000L * 60 *60 *24;//24시간

    @PostConstruct
    protected void init(){
       log.info("키 생성 암호화 전 키 :{}",secret);
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        refreshKey =  Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
        log.info("키 생성 암호화 후 키 :{}",secretKey);
    }


    public TokenDto createToken(String id, List<String> roles){
        log.info("토큰 생성 시작");
        Claims claims = Jwts.claims().setSubject(id);
        claims.put("roles",roles);

        Claims claimsForRefresh = Jwts.claims().setSubject(id);
        Date now = new Date();

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+tokenValidMillisecond))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setClaims(claimsForRefresh)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+refreshValidMillisecond))
                .signWith(refreshKey,SignatureAlgorithm.HS256)
                .compact();
        log.info("토큰 생성 완료");
        return TokenDto.of(accessToken,refreshToken);
    }


    public Authentication getAuthentication(String token){
        log.info("토큰 인증 정보 조회 시작");
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));
        log.info("토큰 인증 정보 조회 완료 user:{}",userDetails.getUsername());
        log.info("토큰 인증 정보 조회 완료 user:{}",userDetails.getAuthorities());
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }

    public String getUsername(String token){
        log.info("토큰으로 회원 정보 추출");
        try {
            String info = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
            log.info("토큰으로 회원 정보 추출 완료 info:{}",info);
            return info;
        } catch (MalformedJwtException ex){
            throw new CustomException(CustomErrorCode.UNSUPPORTED_TOKEN);
        }catch (ExpiredJwtException ex){
            throw new CustomException(CustomErrorCode.EXPIRED_TOKEN);
        }catch (IllegalArgumentException ex){
            throw new CustomException(CustomErrorCode.UNKNOWN_TOKEN_ERROR);
        }

    }
    public String resolveToken(HttpServletRequest request){
        log.info("헤더에서 토큰 값 추출");
        return request.getHeader("Auth");
    }

    public boolean validateToken(String token){
        log.info("토큰 유효성 검증 시작");
        try{
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        }catch (Exception e){
            log.info("토큰 유효 체크 예외 발생");
            return false;
        }
    }




}
