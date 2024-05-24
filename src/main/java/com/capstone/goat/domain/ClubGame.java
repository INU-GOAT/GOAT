package com.capstone.goat.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor
public class ClubGame {

    private Long team1Master;

    private Long team2Master;

    private Long team1ClubId;

    private Long team2ClubId;

    private Integer team1Result;

    private Integer team2Result;

    private Long winClubId;

    public void appendTeam1Info(Long team1Master, Long team1ClubId) {
        this.team1Master = team1Master;
        this.team1ClubId = team1ClubId;
    }

    public void appendTeam2Info(Long team2Master, Long team2ClubId) {
        this.team2Master = team2Master;
        this.team2ClubId = team2ClubId;
    }

    public void inputTeam1Result(int result) {
        this.team1Result = result;
    }

    public void inputTeam2Result(int result) {
        this.team2Result = result;
    }

    public boolean determineWinClub() {
        if (team1Result != null && team2Result != null && team1Result * team2Result == -1) {
            winClubId = (team1Result == 1) ? team1ClubId : team2ClubId;
            return true;
        }
        return false;
    }
}
