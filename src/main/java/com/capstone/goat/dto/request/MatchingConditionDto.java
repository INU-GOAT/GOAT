package com.capstone.goat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    public void insertGroupId(int groupId) {
        this.groupId = groupId;
    }

}
