package com.capstone.goat.controller;

import com.capstone.goat.domain.User;
import com.capstone.goat.dto.request.MatchingConditionDto;
import com.capstone.goat.dto.response.ResponseDto;
import com.capstone.goat.service.GameService;
import com.capstone.goat.service.MatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/matching/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MatchingController {

    private final MatchingService matchingService;

    private final GameService gameService;

    @Operation(summary = "매칭 시작", description = "url 바디에 {sport,latitude,longitude,matchingStartTime,startTimeList,startTimeList,preferGender,preferCourt,userCount,groupId}을 json형식으로 보내주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "매칭 시작 성공",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404",description = "매칭 등록 실패",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<?> matchingStart(@AuthenticationPrincipal User user, @Valid @RequestBody MatchingConditionDto matchingConditionDto){

        matchingService.addMatching(matchingConditionDto);

        matchingService.findMatching(matchingConditionDto);

        return new ResponseEntity<>(new ResponseDto(user.getNickname(),"매칭 시작 성공"), HttpStatus.OK);
    }

    @Operation(summary = "매칭 중단", description = "매칭 목록에서 사용자를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "매칭 중단 성공",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404",description = "매칭 중단 실패",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @DeleteMapping
    public ResponseEntity<?> matchingRemove(@AuthenticationPrincipal User user){

        return new ResponseEntity<>(new ResponseDto(user,"매칭 중단 성공"), HttpStatus.OK);
    }
}
