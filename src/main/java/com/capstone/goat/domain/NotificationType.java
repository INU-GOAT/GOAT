package com.capstone.goat.domain;

import lombok.Getter;

@Getter
public enum NotificationType {
    MATCHING("매칭이 완료되었습니다!!"),
    GROUP_INVITE("님이 그룹 초대를 보냈습니다."),
    GROUP_ACCEPT("님이 그룹 초대를 수락했습니다."),
    GROUP_REJECT("님이 그룹 초대를 거절했습니다."),
    CLUB("님이 클럽 가입을 요청하였습니다.");

    private final String message;

    NotificationType(String message) {
        this.message = message;
    }
}
