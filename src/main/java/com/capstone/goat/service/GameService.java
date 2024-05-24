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
import java.util.Objects;
import java.util.Optional;

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
    private final ClubRepository clubRepository;

    private GamePlayingResponseDto toGamePlayingDto(Game game) {

        List<Teammate> team1 = teammateRepository.findAllByGameIdAndTeamNumber(game.getId(), 1);
        List<Teammate> team2 = teammateRepository.findAllByGameIdAndTeamNumber(game.getId(), 2);

        List<UserInfoDto> team1UserInfoList = getUserInfoDtoList(team1, game.getSport());
        List<UserInfoDto> team2UserInfoList = getUserInfoDtoList(team2, game.getSport());

        return GamePlayingResponseDto.of(game, game.getPreferCourts(), team1UserInfoList, team2UserInfoList);
    }

    private List<UserInfoDto> getUserInfoDtoList(List<Teammate> teammates, Sport sport) {
        // 탈퇴한 유저는 닉네임을 "탈퇴한 유저", rating 점수를 0으로 설정
        return teammates.stream()
                .map(teammate -> {
                    String userNickname;
                    int ratingScore;
                    Optional<User> userOptional = userRepository.findById(teammate.getUserId());
                    if (userOptional.isEmpty()) {
                        userNickname = "탈퇴한_유저";
                        ratingScore = 0;
                    } else {
                        User user = userOptional.get();
                        userNickname = user.getNickname();
                        Rating rating = user.getRatings().get(sport);
                        ratingScore = rating != null ? rating.getRatingScore() : 0;
                    }
                    return UserInfoDto.of(teammate.getUserId(), userNickname, ratingScore);
                })
                .toList();
    }

    private GameFinishedResponseDto toGameFinishedDto(Game game, Integer result) {

        return GameFinishedResponseDto.of(game, result);
    }

    public GamePlayingResponseDto getPlayingGame(long userId) {

        User user = getUser(userId);
        // 유저가 게임 중이 아니면 예외
        if (user.getStatus() != Status.GAMING) {
            throw new CustomException(CustomErrorCode.USER_NOT_GAMING);
        }

        Teammate userTeammate = teammateRepository.findFirstByUserIdOrderByIdDesc(user.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.TEAMMATE_NOT_FOUND));
        Game game = userTeammate.getGame();

        return toGamePlayingDto(game);
    }

    public List<GameFinishedResponseDto> getFinishedGameList(long userId) {

        User user = userRepository.getReferenceById(userId);
        List<Teammate> teammateList = teammateRepository.findAllByUserIdOrderByIdDesc(user.getId());

        return teammateList.stream()
                .filter(teammate -> teammate.getGame().getCourt() != null)    // 종료된 Game만 -> 경기장 확정된 게임까지는 포함
                .map(teammate -> toGameFinishedDto(teammate.getGame(), teammate.getResult()))    // GameResponseDto 로 변환
                .toList();
    }

    public List<TeammateResponseDto> getFinishedGameTeammates(long gameId) {

        List<Teammate> teammateList = teammateRepository.findByGameId(gameId);

        return teammateList.stream()
                .map(teammate -> {
                    String userNickname = userRepository.findNicknameById(teammate.getUserId()).orElse("탈퇴한_유저");
                    return TeammateResponseDto.of(teammate, teammate.getUserId(), userNickname);
                })
                .toList();
    }

    @Transactional
    public void determineCourt(long gameId, String court) {

        Game game = getGame(gameId);

        PreferCourt preferCourt = preferCourtRepository.findFirstByCourtAndGameId(court, gameId);
        double latitude = preferCourt.getLatitude();
        double longitude = preferCourt.getLongitude();
        game.determineCourt(court, latitude, longitude);
    }

    @Transactional
    public void finishGame(long gameId, long userId, GameFinishDto gameFinishDto) {

        Game game = getGame(gameId);
        // 게임 시작 전이면 예외
        if (game.getCourt() == null) {
            throw new CustomException(CustomErrorCode.GAME_NOT_STARTED);
        }

        User user = getUser(userId);
        ClubGame clubGame = game.getClubGame();
        if (clubGame != null && gameFinishDto.getResult() != null) {
            // 그룹장인 경우 게임 결과 입력
            if (Objects.equals(userId, clubGame.getTeam1Master())) {
                clubGame.inputTeam1Result(gameFinishDto.getResult());
            } else if (Objects.equals(userId, clubGame.getTeam2Master())) {
                clubGame.inputTeam2Result(gameFinishDto.getResult());
            }
            if (clubGame.determineWinClub()) {
                updateClubResult(clubGame.getTeam1ClubId(), clubGame.getTeam2ClubId(), clubGame.getWinClubId());
            }
        } else if (gameFinishDto.getResult() != null){
            // 점수 조정
            updateRating(user, game.getSport(), gameFinishDto.getResult(), gameFinishDto.getFeedback());
        }


        // 게임 결과 입력
        Teammate teammate = teammateRepository.findByUserIdAndGameId(userId, gameId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.TEAMMATE_NOT_FOUND));
        teammate.updateGameReport(gameFinishDto.getResult(), gameFinishDto.getComment());

        // 대기 중으로 유저 상태 변경
        user.changeStatus(Status.WAITING);
        user.changeVoteAfterEndGame();
    }

    private void updateClubResult(Long club1Id, Long club2Id, Long winClubId) {

        Club club1 = clubRepository.findById(club1Id)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CLUB_NOT_FOUND));
        Club club2 = clubRepository.findById(club2Id)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CLUB_NOT_FOUND));

        club1.updateGameRecord(winClubId);
        club2.updateGameRecord(winClubId);
    }

    private void updateRating(User user, Sport sport, int result, int feedback) {

        // 승리/패배에 따른 점수 조정
        Rating rating = user.getRatings().get(sport);
        rating.updateRating(result, feedback);
    }

    @Transactional
    public boolean voteCourt(Long gameId, String court,String userNickname){
        Game game = getGame(gameId);
        User user = getUser(userNickname);
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
        Game game = getGame(gameId);
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
        Game game = getGame(gameId);
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
        double latitude = preferCourt.getLatitude();
        double longitude = preferCourt.getLongitude();
        game.determineCourt(courtName, latitude, longitude);
    }

    private Game getGame(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.GAME_NOT_FOUND));
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

    private User getUser(String userNickname) {
        return userRepository.findByNickname(userNickname)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

}
