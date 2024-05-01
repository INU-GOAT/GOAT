package com.capstone.goat.dto.request;

import com.capstone.goat.domain.Group;
import com.capstone.goat.domain.MatchMaking;
import com.capstone.goat.domain.Matching;
import com.capstone.goat.domain.Sport;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchingConditionDto {

    private String sport;

    private float latitude;

    private float longitude;

    private LocalDateTime matchingStartTime;

    private List<String> matchStartTimes;

    private String preferCourt;

    public Matching toEntity(int rating, Group group) {
        return Matching.builder()
                .rating(rating)
                .sport(Sport.getSport(sport))
                .latitude(latitude)
                .longitude(longitude)
                .preferCourt(preferCourt)
                .matchingStartTime(matchingStartTime)
                .group(group)
                .build();
    }

    public List<MatchMaking> toMatchMakingList(int userCount, int rating, long groupId){
        return matchStartTimes.stream().map(matchStartTime ->
                MatchMaking.builder()
                        .sport(Sport.getSport(sport))
                        .userCount(userCount)
                        .rating(rating)
                        .latitude(latitude)
                        .longitude(longitude)
                        .preferCourt(preferCourt)
                        .matchingStartTime(matchingStartTime)
                        .matchStartTime(matchStartTime)
                        .groupId(groupId)
                        .build()).collect(Collectors.toList());
    }

}
