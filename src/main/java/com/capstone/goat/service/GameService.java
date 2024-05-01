package com.capstone.goat.service;

import com.capstone.goat.domain.*;
import com.capstone.goat.dto.response.GameResponseDto;
import com.capstone.goat.exception.ex.CustomErrorCode;
import com.capstone.goat.exception.ex.CustomException;
import com.capstone.goat.repository.GameRepository;
import com.capstone.goat.repository.TeamRepository;
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
    private final TeamRepository teamRepository;

    // TODO 도메인 서비스로 이동할 수 있을까?
    private static GameResponseDto toDto (Game game) {

        List<User> team1UserList = game.getTeam1().stream().map(Teammate::getUser).toList();
        List<User> team2UserList = game.getTeam2().stream().map(Teammate::getUser).toList();

        return GameResponseDto.of(game, team1UserList, team2UserList);
    }

    public GameResponseDto getPlayingGame(long userId) {

        User user = userRepository.getReferenceById(userId);
        Teammate userTeammate = teamRepository.findFirstByUserOrderByIdDesc(user).orElseThrow(() -> new NoSuchElementException("해당하는 유저의 teammate가 존재하지 않습니다."));
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
        List<Teammate> teammateList = teamRepository.findAllByUserOrderByIdDesc(user);

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
    public void determineWinTeam(long gameId, int winTeam) {

        Game game = gameRepository.findById(gameId).orElseThrow(() -> new NoSuchElementException("해당하는 게임이 존재하지 않습니다"));

        game.determineWinTeam(winTeam); // game에 승리 팀 저장

        // 각 팀의 승리/패배에 따른 점수 조정
        updateRating(game);
    }

    private void updateRating(Game game) {

        Sport sport = game.getSport();
        int winTeam = game.getWinTeam();

        if (winTeam == 1) {
            game.getTeam1().forEach(teammate -> {
                Rating rating = teammate.getUser().getRatings().get(sport);
                rating.updateRatingByWin();
            });
            game.getTeam2().forEach(teammate -> {
                Rating rating = teammate.getUser().getRatings().get(sport);
                rating.updateRatingByLose();
            });
        } else if (winTeam == 2) {
            game.getTeam1().forEach(teammate -> {
                Rating rating = teammate.getUser().getRatings().get(sport);
                rating.updateRatingByLose();
            });
            game.getTeam2().forEach(teammate -> {
                Rating rating = teammate.getUser().getRatings().get(sport);
                rating.updateRatingByWin();
            });
        } else {
            throw new CustomException(CustomErrorCode.INVALIED_TEAM_NUMBER);
        }
    }

}
