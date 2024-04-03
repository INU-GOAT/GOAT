package com.capstone.goat.config;

import com.capstone.goat.domain.User;
import com.capstone.goat.dto.request.KakaoTokenResponseDto;
import com.capstone.goat.dto.response.KakaoUserResponseDto;
import com.capstone.goat.exception.ex.CustomErrorCode;
import com.capstone.goat.exception.ex.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientKakao {
    private final WebClient webclient;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${client-secret}")
    private String clientSecret;

    public User getUserData(String kakaoToken){
        log.info("카카오토큰을 통해 정보 받아오기 시작:{}",kakaoToken);
        KakaoUserResponseDto kakaoUserResponseDto = webclient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .headers(h -> h.setBearerAuth(kakaoToken))
                .retrieve()
                .bodyToMono(KakaoUserResponseDto.class) // KAKAO의 유저 정보를 넣을 Dto 클래스
                .block();
        log.info("카카오토큰을 통해 정보 받아오기 성공");

        return User.builder()
                .id(Long.valueOf(kakaoUserResponseDto.getId()))
                .roles(Collections.singletonList("ROLE_USER"))
                .build();
    }

    public String getUserKakaoToken(String userCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("grant_type=authorization_code");
        sb.append("&client_id="+clientId);
        sb.append("&redirect_uri="+redirectUri);
        sb.append("&code="+userCode);
        sb.append("&client_secret="+clientSecret);


        // Map을 application/x-www-form-urlencoded 형식의 문자열로 직렬화

        log.info("카카오 토큰 발급 요청 바디:{}",sb);
        KakaoTokenResponseDto kakaoTokenResponseDto = webclient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .headers(httpHeaders -> httpHeaders.set("Content-Type","application/x-www-form-urlencoded;charset=utf-8"))
                .bodyValue(sb.toString())
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,clientResponse -> Mono.just(new CustomException(CustomErrorCode.CODE_ERROR)))
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();
        log.info("발급된 카카오 토큰 :{}",kakaoTokenResponseDto.getAccess_token());
        return kakaoTokenResponseDto.getAccess_token();
    }



}

