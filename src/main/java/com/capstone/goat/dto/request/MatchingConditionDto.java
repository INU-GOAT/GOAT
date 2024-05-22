package com.capstone.goat.dto.request;

import com.capstone.goat.domain.Group;
import com.capstone.goat.domain.MatchMaking;
import com.capstone.goat.domain.Matching;
import com.capstone.goat.domain.Sport;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchingConditionDto {

    @NotBlank(message = "sport는 공백으로 입력할 수 없습니다.")
    private String sport;

    @NotNull(message = "latitude는 비어있을 수 없습니다.")
    private double latitude;

    @NotNull(message = "longitude는 비어있을 수 없습니다.")
    private double longitude;

    @NotNull(message = "matchStartTimes는 비어있을 수 없습니다.")
    private List<String> matchStartTimes;

    @NotNull(message = "preferCourt는 비어있을 수 없습니다.")
    private String preferCourt;

    public static MatchingConditionDto of(String sport, double latitude, double longitude, List<String> matchStartTimes, String preferCourt) {
        return new MatchingConditionDto(sport, latitude, longitude, matchStartTimes, preferCourt);
    }

    public Matching toEntity(int rating, Group group) {
        return Matching.builder()
                .rating(rating)
                .sport(Sport.getSport(sport))
                .latitude(latitude)
                .longitude(longitude)
                .preferCourt(preferCourt)
                .matchingStartTime(LocalDateTime.now())
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
                        .matchingStartTime(LocalDateTime.now())
                        .matchStartTime(matchStartTime)
                        .groupId(groupId)
                        .build()).collect(Collectors.toList());
    }
}
