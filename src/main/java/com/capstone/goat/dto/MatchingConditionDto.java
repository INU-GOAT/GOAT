package com.capstone.goat.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MatchingConditionDto {

    private String sport;

    private Integer latitude;

    private Integer longitude;

    private LocalDateTime matchingStartTime;

    private List<String> startTimeList;

    private String preferGender;

    private String preferCourt;

    private Integer userCount;

    private Integer groupId;

}
