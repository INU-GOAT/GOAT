package com.capstone.goat.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Sport {

    SOCCER("축구",11),
    BASKETBALL("농구",5),
    BADMINTON("배드민턴",2),
    TABLE_TENNIS("탁구",2);

    private final String name;
    private final int player;

    public static Sport getSport(String inputName) {
        return Arrays.stream(Sport.values())
                .filter(sport -> sport.getName().equals(inputName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 이름의 스포츠를 찾을 수 없습니다"));
    }
}
