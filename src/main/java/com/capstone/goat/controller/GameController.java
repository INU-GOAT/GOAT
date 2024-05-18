package com.capstone.goat.controller;

import com.capstone.goat.domain.User;
import com.capstone.goat.dto.request.GameFinishDto;
import com.capstone.goat.dto.response.GameFinishedResponseDto;
import com.capstone.goat.dto.response.GamePlayingResponseDto;
import com.capstone.goat.dto.response.ResponseDto;
import com.capstone.goat.dto.response.TeammateResponseDto;
import com.capstone.goat.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class GameController {

    private final GameService gameService;

    @Operation(summary = "진행 중인 게임 조회", description = "사용자가 현재 진행 중인 게임을 조회합니다. 진행 중인 게임이 없을 경우 null을 반환합니다.")
    @GetMapping
    public ResponseEntity<?> gamePlaying(@Schema(hidden = true) @AuthenticationPrincipal User user){
        log.info("진행중인 게임 조회 id:{}",user.getId());
        GamePlayingResponseDto gamePlayingResponseDto = gameService.getPlayingGame(user.getId());

        return new ResponseEntity<>(new ResponseDto<>(gamePlayingResponseDto,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "게임 전적 목록 조회", description = "사용자의 전적 목록을 조회합니다.")
    @GetMapping("/finished")
    public ResponseEntity<?> gameFinishedList(@Schema(hidden = true) @AuthenticationPrincipal User user){
        log.info("게임전적조회 조회 id : {}",user.getId());
        List<GameFinishedResponseDto> gameFinishedResponseDtoList = gameService.getFinishedGameList(user.getId());

        return new ResponseEntity<>(new ResponseDto<>(gameFinishedResponseDtoList,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "게임 전적 선수 목록 조회", description = "사용자 전적의 선수들 정보 목록을 조회합니다.")
    @GetMapping("/{gameId}")
    public ResponseEntity<?> gameFinishedTeammates(@PathVariable Long gameId){
        log.info("게임 전적 상세조회 gameId:{}",gameId);
        List<TeammateResponseDto> teammateResponseDtoList = gameService.getFinishedGameTeammates(gameId);

        return new ResponseEntity<>(new ResponseDto(teammateResponseDtoList,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "경기장 확정", description = "게임의 경기장을 확정합니다. url 바디에 {court}를 json 형식으로 보내주세요.")
    @PatchMapping({"/{gameId}/court"})
    public ResponseEntity<?> gameCourtPatch(@PathVariable Long gameId, @RequestBody Map<String, String> param){  // court
        log.info("경기장 확정 조회 gameId:{} ",gameId);
        // 파라미터 검증
        String court = param.get("court");
        if (court == null) throw new IllegalArgumentException("court 형식이 잘못되었습니다.");

        gameService.determineCourt(gameId, court);

        return new ResponseEntity<>(new ResponseDto(gameId,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "진행 중인 게임 종료", description = "사용자가 현재 진행하던 게임을 종료합니다. url 바디에 {result, comment}을 json 형식으로 보내주세요. result는 경기 결과를 받아 승리 시 1, 패배 시 -1, 무승부 시 0을 적어주세요.")
    @PatchMapping("/{gameId}")
    public ResponseEntity<?> gameFinishPatch(@Schema(hidden = true) @AuthenticationPrincipal User user,
                                             @PathVariable Long gameId, @Valid @RequestBody GameFinishDto gameFinishDto){

        log.info("진행 중인 게임 종료 gameId : {}",gameId);

        gameService.finishGame(gameId, user.getId(), gameFinishDto);

        return new ResponseEntity<>(new ResponseDto(null,"성공"), HttpStatus.OK);
    }


}
