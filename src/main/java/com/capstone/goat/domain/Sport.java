package com.capstone.goat.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Sport {

    SOCCER("축구",22),
    FUTSAL_5("풋살-5인",10),
    FUTSAL_6("풋살-6인",12),
    BASKETBALL("농구",10),
    BADMINTON_1("배드민턴-단식",2),
    BADMINTON_2("배드민턴-복식",4);

    private final String name;
    private final int player;

    public static Sport getSport(String inputName) {
        return Arrays.stream(Sport.values())
                .filter(sport -> sport.getName().equals(inputName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 이름의 스포츠를 찾을 수 없습니다"));
    }
}
