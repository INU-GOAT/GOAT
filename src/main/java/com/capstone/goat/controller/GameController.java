package com.capstone.goat.controller;

import com.capstone.goat.domain.User;
import com.capstone.goat.dto.request.GameCourtDto;
import com.capstone.goat.dto.request.GameFinishDto;
import com.capstone.goat.dto.response.*;
import com.capstone.goat.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class GameController {

    private final GameService gameService;

    @Operation(summary = "진행 중인 게임 조회", description = "사용자가 현재 진행 중인 게임을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = GamePlayingResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "[USER_NOT_FOUND] 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "[TEAMMATE_NOT_FOUND] 존재하지 않는 팀원입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[USER_NOT_GAMING] 유저가 게임 중이 아닙니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
    })
    @GetMapping
    public ResponseEntity<?> gamePlaying(@Schema(hidden = true) @AuthenticationPrincipal User user){

        log.info("진행중인 게임 조회 id:{}",user.getId());

        GamePlayingResponseDto gamePlayingResponseDto = gameService.getPlayingGame(user.getId());

        return new ResponseEntity<>(new ResponseDto<>(gamePlayingResponseDto,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "게임 전적 목록 조회", description = "사용자의 전적 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = GameFinishedResponseDto.class)))),
    })
    @GetMapping("/finished")
    public ResponseEntity<?> gameFinishedList(@Schema(hidden = true) @AuthenticationPrincipal User user){

        log.info("게임전적조회 조회 id : {}",user.getId());

        List<GameFinishedResponseDto> gameFinishedResponseDtoList = gameService.getFinishedGameList(user.getId());

        return new ResponseEntity<>(new ResponseDto<>(gameFinishedResponseDtoList,"성공"), HttpStatus.OK);
    }


    @Operation(summary = "게임 전적 선수 목록 조회", description = "사용자 전적의 선수들 정보 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TeammateResponseDto.class)))),
    })
    @GetMapping("/{gameId}")
    public ResponseEntity<?> gameFinishedTeammates(@PathVariable Long gameId){

        log.info("게임 전적 상세조회 gameId:{}",gameId);

        List<TeammateResponseDto> teammateResponseDtoList = gameService.getFinishedGameTeammates(gameId);

        return new ResponseEntity<>(new ResponseDto(teammateResponseDtoList,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "경기장 확정", description = "게임의 경기장을 확정합니다. url 바디에 {court}를 json 형식으로 보내주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "경기장 확정 성공, 게임 Id 반환", content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "400", description = "[BAD_REQUEST] 유효성 검사 예외 발생", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[GAME_NOT_FOUND] 존재하지 않는 게임입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
    })
    @PatchMapping({"/{gameId}/court"})
    public ResponseEntity<?> gameCourtPatch(@PathVariable Long gameId, @Valid @RequestBody GameCourtDto gameCourtDto){

        log.info("경기장 확정 조회 gameId:{} ",gameId);

        gameService.determineCourt(gameId, gameCourtDto.getCourt());

        return new ResponseEntity<>(new ResponseDto(gameId,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "진행 중인 게임 종료", description = "사용자가 현재 진행하던 게임을 종료합니다. url 바디에 {result, comment, feedback}을 json 형식으로 보내주세요. result는 경기 결과를 받아 승리 시 1, 패배 시 -1, 무승부 시 0을 적어주세요. feedback은 사용자에게 매칭된 게임의 수준을 묻고 쉬웠다는 1, 만족한다는 0, 어려웠다는 -1을 적어주세요")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게임 종료 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "[BAD_REQUEST] 유효성 검사 예외 발생", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "[GAME_NOT_STARTED] 게임이 아직 시작하지 않았습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[GAME_NOT_FOUND] 존재하지 않는 게임입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[TEAMMATE_NOT_FOUND] 존재하지 않는 팀원입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[USER_NOT_FOUND] 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
    })
    @PatchMapping("/{gameId}")
    public ResponseEntity<?> gameFinishPatch(@Schema(hidden = true) @AuthenticationPrincipal User user,
                                             @PathVariable Long gameId, @Valid @RequestBody GameFinishDto gameFinishDto){

        log.info("진행 중인 게임 종료 gameId : {}",gameId);

        gameService.finishGame(gameId, user.getId(), gameFinishDto);

        return new ResponseEntity<>(new ResponseDto<>(null,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "경기장 투표 내역 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "경기장 투표 내역 조회 성공", content = @Content(schema = @Schema(implementation = VoteTotalResponseDto.class)))
    })
    @GetMapping("/vote/{gameId}")
    public ResponseEntity<ResponseDto<VoteTotalResponseDto>> getVoteNow(@PathVariable Long gameId){
        return new ResponseEntity<>(new ResponseDto<>(gameService.getVoteMessage(gameId),"경기장 투표 내역 조회"),HttpStatus.OK);
    }


    private static final String CLUB_GAME_DATA = """
            {
              "data": [
                {
                  "gameId": 1,
                  "sportName": "탁구",
                  "startTime": "2024-05-25T01:30:00",
                  "parsedDate": "2024-05-25",
                  "parsedTime": "01:30",
                  "court": "청와대 본관",
                  "clubGame": {
                    "team1Master": 1,
                    "team2Master": 4,
                    "team1ClubId": 1,
                    "team2ClubId": 2,
                    "team1Result": -1,
                    "team2Result": 1,
                    "winClubId": 2
                  },
                  "result": -1
                }
              ],
              "msg": "성공"
            }
            """;
}
