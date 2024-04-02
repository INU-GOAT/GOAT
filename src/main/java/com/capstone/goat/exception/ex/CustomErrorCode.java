package com.capstone.goat.exception.ex;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 유저입니다."),
    WRONG_TYPE_TOKEN(HttpStatus.UNAUTHORIZED,"토큰의 서명이 유효하지 않습니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED,"잘못된 형식의 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,"만료된 토큰입니다."),
    UNKNOWN_TOKEN_ERROR(HttpStatus.BAD_REQUEST,"토큰의 값이 존재하지 않습니다."),
    ID_NOT_FOUND(HttpStatus.UNAUTHORIZED,"아이디가 존재하지 않습니다."),
    PASSWORD_NOT_MATCHED(HttpStatus.UNAUTHORIZED,"비밀번호가 틀립니다."),
    NEED_JOIN(HttpStatus.OK,"회원가입이 필요합니다."),
    CODE_ERROR(HttpStatus.UNAUTHORIZED,"이미 한번 사용된 코드이거나, 코드 형식에 문제가 있습니다."),
    HAS_CLUB(HttpStatus.BAD_REQUEST,"이미 가입된 클럽이 있습니다.");


    private final HttpStatus status;
    private final String message;
}
