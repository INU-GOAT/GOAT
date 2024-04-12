package com.capstone.goat.controller;

import com.capstone.goat.domain.Group;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.request.MatchingConditionDto;
import com.capstone.goat.dto.response.ResponseDto;
import com.capstone.goat.service.GroupService;
import com.capstone.goat.service.MatchMakingService;
import com.capstone.goat.service.RatingService;
import com.capstone.goat.service.UserService;
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

    private final MatchMakingService matchMakingService;
    private final GroupService groupService;
    private final UserService userService;
    private final RatingService ratingService;

    @Operation(summary = "매칭 시작", description = "url 바디에 {sport,latitude,longitude,matchingStartTime,matchStartTimes,preferCourt,userCount,groupId}을 json형식으로 보내주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "매칭 시작 성공",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404",description = "매칭 등록 실패",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<?> matchingStart(@AuthenticationPrincipal User user, @Valid @RequestBody MatchingConditionDto matchingConditionDto){

        // 사용자가 그룹이 없으면 생성
        if (matchingConditionDto.getGroupId() == null) {
            Group group = groupService.addGroup(user.getId());  // (long userId)
            matchingConditionDto.insertGroupId(group.getId());  // (long groupId)
        }

        // 그룹원의 평균 rating을 계산
        int rating = ratingService.getRatingMean(matchingConditionDto.getGroupId(), matchingConditionDto.getSport());  // (long groupId,String sport)

        matchMakingService.addMatchingAndMatchMaking(matchingConditionDto, rating);  // Controller to Service 용 dto 만드는 것도 좋음

        matchMakingService.findMatching(matchingConditionDto, rating);

        return new ResponseEntity<>(new ResponseDto(user.getNickname(),"매칭 시작 성공"), HttpStatus.OK);
    }

    @Operation(summary = "매칭 중단", description = "매칭 목록에서 사용자를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "매칭 중단 성공",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400",description = "그룹장이 아닙니다. 그룹장만 매칭 중단을 할 수 있습니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @DeleteMapping
    public ResponseEntity<?> matchingRemove(@AuthenticationPrincipal User user){

        Group group = user.getGroup();
        if (!user.getId().equals(group.getId()))
            return new ResponseEntity<>(new ResponseDto(group.getMaster().getNickname(), "그룹장이 아닙니다. 그룹장만 매칭 중단을 할 수 있습니다."), HttpStatus.BAD_REQUEST);

        matchMakingService.deleteMatching(group.getId());

        return new ResponseEntity<>(new ResponseDto(user.getNickname(),"매칭 중단 성공"), HttpStatus.OK);
    }
}
