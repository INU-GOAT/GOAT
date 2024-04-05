package com.capstone.goat.controller;

import com.capstone.goat.domain.User;
import com.capstone.goat.dto.response.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GameController {

    /*@Operation(summary = "진행 중인 게임 조회", description = "사용자가 현재 진행하고 있는 게임을 조회합니다.")
    @GetMapping("playing")
    public ResponseEntity<?> gamePlayingDetails(@AuthenticationPrincipal User user){
        return new ResponseEntity<>(new ResponseDto(null,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "진행 중인 게임 종료", description = "사용자가 현재 진행하던 게임을 종료합니다. url 바디에 {winTeam}을 json 형식으로 보내주세요. winTeam은 team1과 team2 중 하나를 String 형태로 적어주세요.")
    @PutMapping("playing")
    public ResponseEntity<?> gameModify(@AuthenticationPrincipal User user, String winTeam){
        return new ResponseEntity<>(new ResponseDto(null,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "게임 전적 목록 조회", description = "사용자의 전적 목록을 조회합니다.")
    @GetMapping()
    public ResponseEntity<?> gameFinishedList(@AuthenticationPrincipal User user){
        return new ResponseEntity<>(new ResponseDto(null,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "게임 전적 상세 조회", description = "사용자 전적의 게임 중 하나를 상세 조회합니다.")
    @GetMapping("{gameId}")
    public ResponseEntity<?> gameFinishedDetails(@PathVariable Integer gameId, @AuthenticationPrincipal User user){
        return new ResponseEntity<>(new ResponseDto(null,"성공"), HttpStatus.OK);
    }*/


}
