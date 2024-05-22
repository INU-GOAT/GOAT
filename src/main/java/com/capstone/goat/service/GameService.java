package com.capstone.goat.service;

import com.capstone.goat.domain.*;
import com.capstone.goat.dto.request.GameFinishDto;
import com.capstone.goat.dto.response.*;
import com.capstone.goat.exception.ex.CustomErrorCode;
import com.capstone.goat.exception.ex.CustomException;
import com.capstone.goat.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final TeammateRepository teammateRepository;
    private final VotedCourtRepository votedCourtRepository;
    private final PreferCourtRepository preferCourtRepository;

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

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
        // 유저가 게임 중이 아니면 예외
        if (user.getStatus() != Status.GAMING) {
            throw new CustomException(CustomErrorCode.USER_NOT_GAMING);
        }

        Teammate userTeammate = teammateRepository.findFirstByUserOrderByIdDesc(user)
                .orElseThrow(() -> new CustomException(CustomErrorCode.TEAMMATE_NOT_FOUND));
        Game game = userTeammate.getGame();

        return toGamePlayingDto(game);
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

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.GAME_NOT_FOUND));

        PreferCourt preferCourt = preferCourtRepository.findFirstByCourtAndGameId(court, gameId);
        float latitude = preferCourt.getLatitude();
        float longitude = preferCourt.getLongitude();
        game.determineCourt(court, latitude, longitude);
    }

    @Transactional
    public void finishGame(long gameId, long userId, GameFinishDto gameFinishDto) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.GAME_NOT_FOUND));
        // 게임 시작 전이면 예외
        if (game.getCourt() == null) {
            throw new CustomException(CustomErrorCode.GAME_NOT_STARTED);
        }

        // 게임 결과 입력
        Teammate teammate = teammateRepository.findByUserIdAndGameId(userId, gameId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.TEAMMATE_NOT_FOUND));
        teammate.updateGameReport(gameFinishDto.getResult(), gameFinishDto.getComment());

        // 대기 중으로 유저 상태 변경
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
        user.changeStatus(Status.WAITING);
        user.changeVoteAfterEndGame();

        // 점수 조정
        updateRating(user, game.getSport(), gameFinishDto.getResult(), gameFinishDto.getFeedback());
    }

    private void updateRating(User user, Sport sport, int result, int feedback) {

        // 승리/패배에 따른 점수 조정
        Rating rating = user.getRatings().get(sport);
        rating.updateRating(result, feedback);
    }

    @Transactional
    public boolean voteCourt(Long gameId, String court,String userNickname){
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.GAME_NOT_FOUND));
        User user = userRepository.findByNickname(userNickname)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
        if(user.getIsVoted()){
            return false;
        }
        else {
            if (votedCourtRepository.existsByCourtAndGameId(court,gameId)) {
                VotedCourt votedCourt = votedCourtRepository.findByCourtAndGameId(court,gameId).get();
                votedCourt.upCount();
            } else {
                votedCourtRepository.save(VotedCourt.builder().court(court).game(game).build());
            }

            user.vote();
            return true;
        }
    }

    @Transactional
    public VoteTotalResponseDto getVoteMessage(Long gameId){
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.GAME_NOT_FOUND));
        List<VotedCourt> courts = votedCourtRepository.findAllByGameId(gameId);
        int voteCount = 0;
        List<VotedCourtResponseDto> list = new ArrayList<>();
        for(VotedCourt court : courts){
            voteCount += court.getCount();
            list.add(VotedCourtResponseDto.of(court));
        }
        if(voteCount==game.getSport().getPlayer()*2){
            determineCourt(gameId);
        }
         return VoteTotalResponseDto.of(list,game.getSport().getPlayer()*2-voteCount);
    }

    @Transactional
    public void determineCourt(Long gameId){
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.GAME_NOT_FOUND));
        List<VotedCourt> courts = votedCourtRepository.findAllByGameId(gameId);
        int max = courts.get(0).getCount();
        String courtName =courts.get(0).getCourt();
        for(VotedCourt court : courts) {
            if(court.getCount()>max){
                max = court.getCount();
                courtName = court.getCourt();
            }
        }

        PreferCourt preferCourt = preferCourtRepository.findFirstByCourtAndGameId(courtName, gameId);
        float latitude = preferCourt.getLatitude();
        float longitude = preferCourt.getLongitude();
        game.determineCourt(courtName, latitude, longitude);
    }



}
