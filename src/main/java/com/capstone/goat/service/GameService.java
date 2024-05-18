package com.capstone.goat.service;

import com.capstone.goat.domain.*;
import com.capstone.goat.dto.request.GameFinishDto;
import com.capstone.goat.dto.response.GameFinishedResponseDto;
import com.capstone.goat.dto.response.GamePlayingResponseDto;
import com.capstone.goat.dto.response.TeammateResponseDto;
import com.capstone.goat.dto.response.UserInfoDto;
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

    private GamePlayingResponseDto toGamePlayingDto(Game game) {

        List<Teammate> team1 = teammateRepository.findAllByGameIdAndTeamNumber(game.getId(), 1);
        List<Teammate> team2 = teammateRepository.findAllByGameIdAndTeamNumber(game.getId(), 2);

        List<UserInfoDto> team1UserInfoList = team1.stream()
                .map(teammate -> UserInfoDto.of(teammate.getUser(), game.getSport()))
                .toList();
        List<UserInfoDto> team2UserInfoList = team2.stream()
                .map(teammate -> UserInfoDto.of(teammate.getUser(), game.getSport()))
                .toList();


        return GamePlayingResponseDto.of(game, game.getPreferCourts(), team1UserInfoList, team2UserInfoList);
    }

    private GameFinishedResponseDto toGameFinishedDto(Game game, Integer result) {

        List<Teammate> team1 = teammateRepository.findAllByGameIdAndTeamNumber(game.getId(), 1);
        List<Teammate> team2 = teammateRepository.findAllByGameIdAndTeamNumber(game.getId(), 2);

        return GameFinishedResponseDto.of(game, result);
    }

    public GamePlayingResponseDto getPlayingGame(long userId) {

        User user = userRepository.getReferenceById(userId);
        Teammate userTeammate = teammateRepository.findFirstByUserOrderByIdDesc(user).orElseThrow(() -> new NoSuchElementException("해당하는 유저의 teammate가 존재하지 않습니다."));
        Game game = userTeammate.getGame();

        GamePlayingResponseDto gamePlayingResponseDto = null; // 이미 종료된 게임이면 null 객체 반환

        // 진행 중인 게임일 경우 GameResponseDto 로 변환
        if (user.getStatus() == Status.GAMING) {
            gamePlayingResponseDto = toGamePlayingDto(game);
        }

        return gamePlayingResponseDto;
    }

    public List<GameFinishedResponseDto> getFinishedGameList(long userId) {

        User user = userRepository.getReferenceById(userId);
        List<Teammate> teammateList = teammateRepository.findAllByUserOrderByIdDesc(user);

        return teammateList.stream()
                .filter(teammate -> teammate.getGame().getCourt() != null)    // 종료된 Game만 -> 경기장 확정된 게임까지는 포함
                .map(teammate -> toGameFinishedDto(teammate.getGame(), teammate.getResult()))    // GameResponseDto 로 변환
                .toList();
    }

    public List<TeammateResponseDto> getFinishedGameTeammates(long gameId) {

        List<Teammate> teammateList = teammateRepository.findByGameId(gameId);

        return teammateList.stream()
                .map(teammate -> TeammateResponseDto.of(teammate, teammate.getUser()))
                .toList();
    }

    @Transactional
    public void determineCourt(long gameId, String court) {

        Game game = gameRepository.findById(gameId).orElseThrow(() -> new NoSuchElementException("해당하는 게임이 존재하지 않습니다"));

        game.determineCourt(court);
    }

    @Transactional
    public void finishGame(long gameId, long userId, GameFinishDto gameFinishDto) {

        Teammate teammate = teammateRepository.findByUserIdAndGameId(userId, gameId)
                .orElseThrow(() -> new NoSuchElementException("해당하는 Teammate가 존재하지 않습니다"));
        teammate.updateGameReport(gameFinishDto.getResult(), gameFinishDto.getComment());

        // 대기 중으로 유저 상태 변경
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("해당하는 유저가 존재하지 않습니다"));
        user.changeStatus(Status.WAITING);

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new NoSuchElementException("해당하는 게임이 존재하지 않습니다"));
        updateRating(user, game.getSport(), gameFinishDto.getResult());
    }

    private void updateRating(User user, Sport sport, Integer result) {

        // 승리/패배에 따른 점수 조정
        Rating rating = user.getRatings().get(sport);
        if (result == 1) {
            rating.updateRatingByWin();
        } else if (result == -1) {
            rating.updateRatingByLose();
        } else if (result == 0) {
            rating.updateRatingByDraw();
        }
    }

}
