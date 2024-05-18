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
    HAS_CLUB(HttpStatus.BAD_REQUEST,"이미 가입된 클럽이 있습니다."),
    CLUB_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 클럽입니다."),
    APPLYING_CLUB_EXIST(HttpStatus.BAD_REQUEST,"이미 가입신청한 클럽이나, 가입된 클럽이있습니다."),
    ONLY_MASTER_AUTH(HttpStatus.UNAUTHORIZED,"클럽장만이 권한이 있습니다."),
    DUPLICATE_CLUB_NAME(HttpStatus.BAD_REQUEST,"이미 존재하는 클럽이름입니다."),
    HAS_NOT_CLUB(HttpStatus.BAD_REQUEST,"가입된 클럽이 없습니다."),
    MASTER_NOT_OUT(HttpStatus.BAD_REQUEST,"클럽장은 탈퇴가 불가능합니다."),
    NOT_APPLY_USER(HttpStatus.BAD_REQUEST,"클럽에 가입 신청을 하지 않은 유저입니다."),
    EXIST_NICKNAME(HttpStatus.BAD_REQUEST,"이미 존재하는 닉네임입니다."),
    FULL_MEMBER(HttpStatus.BAD_REQUEST,"클럽의 인원이 최대입니다."),
    EXIST_ID(HttpStatus.BAD_REQUEST,"이미 존재하는 id값 입니다."),
    GROUP_INVITING_ON_GOING(HttpStatus.CONFLICT, "그룹원을 초대 중이므로 매칭 시작이 불가능합니다."),
    MATCHING_ACCESS_DENIED(HttpStatus.BAD_REQUEST, "그룹장이 아닙니다. 그룹장만 매칭 조작을 할 수 있습니다."),
    NO_JOINING_GROUP(HttpStatus.NOT_FOUND, "가입된 그룹을 찾을 수 없습니다."),
    INVALID_GAME_RESULT(HttpStatus.BAD_REQUEST, "잘못된 result 값입니다."),
    USER_BELONG_GROUP(HttpStatus.BAD_REQUEST,"그룹에 초대할 수 없습니다. 해당 유저가 이미 그룹에 속해있습니다."),
    USER_INVITED_GROUP(HttpStatus.BAD_REQUEST,"그룹에 초대할 수 없습니다. 해당 유저가 그룹 초대를 받는 중입니다.");


    private final HttpStatus status;
    private final String message;
}
