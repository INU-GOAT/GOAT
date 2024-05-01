package com.capstone.goat.dto.response;

import com.capstone.goat.domain.Game;
import com.capstone.goat.domain.Sport;
import com.capstone.goat.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class GameResponseDto {

    private final long id;

    private final String sportName;

    private final LocalDateTime startTime;

    private final float latitude;

    private final float longitude;

    private final String court;

    private final Integer winTeam;    // null이면 게임 중 / 1이면 1팀 / 2면 2팀

    private final List<UserInfo> team1;

    private final List<UserInfo> team2;


    @Builder(access = AccessLevel.PRIVATE)
    private GameResponseDto(long id, String sportName, LocalDateTime startTime, float latitude, float longitude, String court, Integer winTeam, List<UserInfo> team1, List<UserInfo> team2) {
        this.id = id;
        this.sportName = sportName;
        this.startTime = startTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.court = court;
        this.winTeam = winTeam;
        this.team1 = team1;
        this.team2 = team2;
    }

    public static GameResponseDto of(Game game, List<User> team1UserList, List<User> team2UserList) {
        List<UserInfo> team1 = team1UserList.stream().map(user -> UserInfo.of(user, game.getSport())).toList();
        List<UserInfo> team2 = team2UserList.stream().map(user -> UserInfo.of(user, game.getSport())).toList();

        return GameResponseDto.builder()
                .id(game.getId())
                .sportName(game.getSport().getName())
                .startTime(game.getStartTime())
                .latitude(game.getLatitude())
                .longitude(game.getLongitude())
                .court(game.getCourt())
                .winTeam(game.getWinTeam())
                .team1(team1)
                .team2(team2)
                .build();
    }

    @Getter
    private static class UserInfo {

        private final long userId;

        private final String nickname;

        private final int ratingScore;

        @Builder(access = AccessLevel.PRIVATE)
        private UserInfo(long userId, String nickname, int ratingScore) {
            this.userId = userId;
            this.nickname = nickname;
            this.ratingScore = ratingScore;
        }

        private static UserInfo of(User user, Sport sport) {
            return UserInfo.builder()
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .ratingScore(user.getRatings().get(sport).getRatingScore())
                    .build();
        }
    }
}
