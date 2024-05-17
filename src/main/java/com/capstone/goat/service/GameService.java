package com.capstone.goat.service;

import com.capstone.goat.domain.*;
import com.capstone.goat.dto.response.GameResponseDto;
import com.capstone.goat.exception.ex.CustomErrorCode;
import com.capstone.goat.exception.ex.CustomException;
import com.capstone.goat.repository.GameRepository;
import com.capstone.goat.repository.TeammateRepository;
import com.capstone.goat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final TeammateRepository teammateRepository;

    private GameResponseDto toDto (Game game) {

        List<Teammate> team1 = teammateRepository.findAllByGameIdAndTeamNumber(game.getId(), 1);
        List<Teammate> team2 = teammateRepository.findAllByGameIdAndTeamNumber(game.getId(), 2);

        List<User> team1UserList = team1.stream().map(Teammate::getUser).toList();
        List<User> team2UserList = team2.stream().map(Teammate::getUser).toList();

        return GameResponseDto.of(game, game.getPreferCourts(), team1UserList, team2UserList);
    }

    public GameResponseDto getPlayingGame(long userId) {

        User user = userRepository.getReferenceById(userId);
        Teammate userTeammate = teammateRepository.findFirstByUserOrderByIdDesc(user).orElseThrow(() -> new NoSuchElementException("해당하는 유저의 teammate가 존재하지 않습니다."));
        Game game = userTeammate.getGame();

        GameResponseDto gameResponseDto = null; // 이미 종료된 게임이면 null 객체 반환

        // 진행 중인 게임일 경우 GameResponseDto 로 변환
        if (game.getWinTeam() == null) {
            gameResponseDto = toDto(game);
        }

        return gameResponseDto;
    }

    public List<GameResponseDto> getFinishedGame(long userId) {

        User user = userRepository.getReferenceById(userId);
        List<Teammate> teammateList = teammateRepository.findAllByUserOrderByIdDesc(user);

        return teammateList.stream()
                .filter(teammate -> teammate.getGame().getWinTeam() != null)    // 종료된 Game 만
                .map(teammate -> toDto(teammate.getGame()))    // GameResponseDto 로 변환
                .toList();
    }

    public GameResponseDto getFinishedGameDetails(long gameId) {

        Game game = gameRepository.findById(gameId).orElseThrow(() -> new NoSuchElementException("해당하는 게임이 존재하지 않습니다"));

        return toDto(game);
    }

    @Transactional
    public void determineCourt(long gameId, String court) {

        Game game = gameRepository.findById(gameId).orElseThrow(() -> new NoSuchElementException("해당하는 게임이 존재하지 않습니다"));

        game.determineCourt(court);
    }

    @Transactional
    public void determineWinTeam(long gameId, long userId, int winTeam) {

        Game game = gameRepository.findById(gameId).orElseThrow(() -> new NoSuchElementException("해당하는 게임이 존재하지 않습니다"));

        VotedWinTeam votedWinTeam = VotedWinTeam.builder()
                .userId(userId)
                .winTeam(winTeam)
                .game(game)
                .build();

        game.voteWinTeam(votedWinTeam);
        
        // 대기 중으로 유저 상태 변경
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("해당하는 유저가 존재하지 않습니다"));
        user.changeStatus(Status.WAITING);

        int player = game.getSport().getPlayer();
        if (game.getVotedWinTeams().size() == player * 2) {
            int winTeam1Count = 0;

            for (VotedWinTeam userWinTeam : game.getVotedWinTeams()) {
                if (userWinTeam.getWinTeam() == 1) {
                    winTeam1Count++;
                }
            }

            if (winTeam1Count < player) {
                game.determineWinTeam(2);
            } else if (winTeam1Count > player) {
                game.determineWinTeam(1);
            } else {
                game.determineWinTeam(0);
            }

            // 각 팀의 승리/패배에 따른 점수 조정
            updateRating(game);
        }
    }

    private void updateRating(Game game) {

        Sport sport = game.getSport();
        int winTeam = game.getWinTeam();

        List<Teammate> team1 = teammateRepository.findAllByGameIdAndTeamNumber(game.getId(), 1);
        List<Teammate> team2 = teammateRepository.findAllByGameIdAndTeamNumber(game.getId(), 2);

        if (winTeam == 1) {
            team1.forEach(teammate -> {
                Rating rating = teammate.getUser().getRatings().get(sport);
                rating.updateRatingByWin();
            });
            team2.forEach(teammate -> {
                Rating rating = teammate.getUser().getRatings().get(sport);
                rating.updateRatingByLose();
            });
        } else if (winTeam == 2) {
            team1.forEach(teammate -> {
                Rating rating = teammate.getUser().getRatings().get(sport);
                rating.updateRatingByLose();
            });
            team2.forEach(teammate -> {
                Rating rating = teammate.getUser().getRatings().get(sport);
                rating.updateRatingByWin();
            });
        } else if (winTeam != 0){
            throw new CustomException(CustomErrorCode.INVALID_TEAM_NUMBER);
        }
    }

}
