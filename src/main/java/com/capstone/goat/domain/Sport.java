package com.capstone.goat.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Sport {

    SOCCER("축구",11),
    FUTSAL_5("풋살-5인",5),
    FUTSAL_6("풋살-6인",6),
    BASKETBALL("농구",5),
    BADMINTON_1("배드민턴-단식",1),
    BADMINTON_2("배드민턴-복식",2);

    private final String name;
    private final int player;

    public static Sport getSport(String inputName) {
        return Arrays.stream(Sport.values())
                .filter(sport -> sport.getName().equals(inputName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 이름의 스포츠를 찾을 수 없습니다"));
    }
}
