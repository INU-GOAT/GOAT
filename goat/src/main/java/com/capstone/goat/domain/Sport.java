package com.capstone.goat.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

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


}
