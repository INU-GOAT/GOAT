package com.capstone.goat.controller;

import com.capstone.goat.domain.NotificationType;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.request.MatchingConditionDto;
import com.capstone.goat.dto.response.MatchingResponseDto;
import com.capstone.goat.dto.response.ResponseDto;
import com.capstone.goat.exception.ex.CustomErrorCode;
import com.capstone.goat.exception.ex.CustomException;
import com.capstone.goat.repository.NotificationRepository;
import com.capstone.goat.service.MatchMakingService;
import com.capstone.goat.service.MatchingService;
import com.capstone.goat.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
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
import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class MatchingController {

    private final MatchMakingService matchMakingService;
    private final MatchingService matchingService;
    private final RatingService ratingService;
    private final NotificationRepository notificationRepository;;

    @Operation(summary = "매칭 시작", description = "url 바디에 {sport,latitude,longitude,matchStartTimes,preferCourt}을 json형식으로 보내주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매칭 시작 성공, 그룹 Id 반환", content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "400", description = "[BAD_REQUEST] 유효성 검사 예외 발생", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "[NOT_WAITING_STATE] 유저가 매칭 가능한 상태가 아닙니다. 이미 매칭 중이거나 게임이 종료되지 않았습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[USER_NOT_FOUND] USER_NOT_FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[GROUP_NOT_FOUND] 존재하지 않는 그룹입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[MATCHING_ACCESS_DENIED 그룹장이 아닙니다. 그룹장만 매칭 조작을 할 수 있습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "[GROUP_INVITING_ON_GOING] 그룹원을 초대 중이므로 매칭 시작이 불가능합니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
    })
    @PostMapping
    public ResponseEntity<?> matchingStart(@Schema(hidden = true) @AuthenticationPrincipal User user, @Valid @RequestBody MatchingConditionDto matchingConditionDto) {

        long userId = user.getId();
        log.info("매칭 시작 id : {}", userId);

        // 그룹원을 초대 중일 때에는 매칭 시작 불가능
        if (notificationRepository.findSendTimeBySenderIdAndType(userId, NotificationType.GROUP_INVITE).stream()
                .anyMatch(sendTime -> Duration.between(sendTime, LocalDateTime.now()).getSeconds() <= 30)) {
            throw new CustomException(CustomErrorCode.GROUP_INVITING_ON_GOING);
        }

        // 그룹원의 평균 rating을 계산
        int rating = ratingService.getRatingMean(userId, matchingConditionDto.getSport());
        log.info("[로그] rating : {}", rating);
        long groupId = matchMakingService.addMatchingAndMatchMaking(matchingConditionDto, userId, rating);
        log.info("[로그] groupId : {}", groupId);
        matchMakingService.findMatching(matchingConditionDto, groupId, rating);

        return new ResponseEntity<>(new ResponseDto(groupId, "매칭 시작 성공"), HttpStatus.CREATED);
    }

    @Operation(summary = "매칭 중인 조건 조회", description = "사용자가 현재 매칭 중인 조건을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = MatchingResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[USER_NOT_FOUND] 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[NO_JOINING_GROUP] 가입된 그룹을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
    })
    @GetMapping
    public ResponseEntity<?> matchingCondition(@Schema(hidden = true) @AuthenticationPrincipal User user){

        log.info("매칭 중인 조건 조회 id : {}", user.getId());

        MatchingResponseDto matchingResponseDto = matchingResponseDto = matchingService.getMatchingCondition(user.getId());

        return new ResponseEntity<>(new ResponseDto<>(matchingResponseDto,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "매칭 중단", description = "매칭 목록에서 사용자를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "매칭 중단 성공, 유저 Id 반환",content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "404", description = "[USER_NOT_FOUND] 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[NO_JOINING_GROUP] 가입된 그룹을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[MATCHING_ACCESS_DENIED 그룹장이 아닙니다. 그룹장만 매칭 조작을 할 수 있습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[NO_MATCHING] 매칭 중이 아닙니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
    })
    @DeleteMapping
    public ResponseEntity<?> matchingRemove(@Schema(hidden = true) @AuthenticationPrincipal User user){

        log.info("매칭 중단 id : {}",user.getId());

        matchMakingService.deleteMatching(user.getId());

        return new ResponseEntity<>(new ResponseDto(user.getId(),"매칭 중단 성공"), HttpStatus.OK);
    }
}
