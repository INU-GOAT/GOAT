package com.capstone.goat.controller;

import com.capstone.goat.domain.Group;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.request.MatchingConditionDto;
import com.capstone.goat.dto.response.MatchingResponseDto;
import com.capstone.goat.dto.response.ResponseDto;
import com.capstone.goat.exception.ex.CustomErrorCode;
import com.capstone.goat.exception.ex.CustomException;
import com.capstone.goat.repository.NotificationRepository;
import com.capstone.goat.repository.UserRepository;
import com.capstone.goat.service.GroupService;
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
import java.util.Objects;

import static java.util.Optional.ofNullable;

@RestController
@RequestMapping("/api/matching/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class MatchingController {

    private final MatchMakingService matchMakingService;
    private final MatchingService matchingService;
    private final GroupService groupService;
    private final RatingService ratingService;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Operation(summary = "매칭 시작", description = "url 바디에 {sport,latitude,longitude,matchingStartTime,matchStartTimes,preferCourt,userCount,groupId}을 json형식으로 보내주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매칭 시작 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "그룹장이 아닙니다. 그룹장만 매칭 조작을 할 수 있습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "그룹원을 초대 중이므로 매칭 시작이 불가능합니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
    })
    @PostMapping
    public ResponseEntity<?> matchingStart(@Schema(hidden = true) @AuthenticationPrincipal User user, @Valid @RequestBody MatchingConditionDto matchingConditionDto) {
        log.info("매칭 시작 id : {}",user.getId());
        // 그룹원을 초대 중일 때에는 매칭 시작 불가능
        notificationRepository.findSendTimeBySenderIdAndType(user.getId(), 2).forEach(sendTime -> {
            if (Duration.between(sendTime, LocalDateTime.now()).getSeconds() <= 30)
                throw new CustomException(CustomErrorCode.GROUP_INVITING_ON_GOING);
        });

        // TODO user 에서 groupId 가져오지 말고 groupId를 받아오는 방식으로 변경해야 함
        long userId = user.getId();
        user = userRepository.findById(userId).orElseThrow();

        Group group = ofNullable(user.getGroup())
                .orElseGet(() -> groupService.addGroup(userId));  // 사용자에게 그룹이 없으면 그룹 생성

        // TODO 도메인 내부로 이동
        // 그룹장이 아닌 경우 매칭 시작 불가능
        if (!Objects.equals(group.getMaster().getId(), userId)) {
            throw new CustomException(CustomErrorCode.MATCHING_ACCESS_DENIED);
        }

        log.info("[로그] userId = " + userId + " groupId = " + group.getId() + " 매칭 시작");
        // 그룹원의 평균 rating을 계산
        int rating = ratingService.getRatingMean(group.getId(), matchingConditionDto.getSport());  // (long groupId, String sport)
        matchMakingService.addMatchingAndMatchMaking(matchingConditionDto, group.getId(), rating);  // Controller to Service 용 dto 만드는 것도 좋음
        matchMakingService.findMatching(matchingConditionDto, group.getId(), rating);

        return new ResponseEntity<>(new ResponseDto(user.getNickname(), "매칭 시작 성공"), HttpStatus.CREATED);
    }

    @Operation(summary = "매칭 중인 조건 조회", description = "사용자가 현재 매칭 중인 조건을 조회합니다. 매칭 중이 아닐 경우 null을 반환합니다.")
    @GetMapping
    public ResponseEntity<?> matchingCondition(@Schema(hidden = true) @AuthenticationPrincipal User user){
        log.info("매칭 중인 조건 조회 id : {}",user.getId());
        user = userRepository.findById(user.getId()).orElseThrow();

        Group group = user.getGroup();
        MatchingResponseDto matchingResponseDto = null;
        if(group != null) {
            matchingResponseDto = matchingService.getMatchingCondition(user.getGroup().getId());
        }

        return new ResponseEntity<>(new ResponseDto<>(matchingResponseDto,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "매칭 중단", description = "매칭 목록에서 사용자를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "매칭 중단 성공",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400",description = "그룹장이 아닙니다. 그룹장만 매칭 조작을 할 수 있습니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404",description = "가입된 그룹을 찾을 수 없습니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @DeleteMapping
    public ResponseEntity<?> matchingRemove(@Schema(hidden = true) @AuthenticationPrincipal User user){
        log.info("매칭 중단 id : {}",user.getId());
        user = userRepository.findById(user.getId()).orElseThrow();

        // 그룹이 존재하지 않을 경우 예외
        Group group = ofNullable(user.getGroup())
                .orElseThrow(() -> new CustomException(CustomErrorCode.NO_JOINING_GROUP));

        // 그룹장이 아닌 경우 매칭 종료 불가능
        if (!Objects.equals(group.getMaster().getId(), user.getId()))
            throw new CustomException(CustomErrorCode.MATCHING_ACCESS_DENIED);

        matchMakingService.deleteMatching(group.getId());

        return new ResponseEntity<>(new ResponseDto(user.getNickname(),"매칭 중단 성공"), HttpStatus.OK);
    }
}
