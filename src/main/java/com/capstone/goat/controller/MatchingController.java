package com.capstone.goat.controller;

import com.capstone.goat.domain.Group;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.request.MatchingConditionDto;
import com.capstone.goat.dto.response.ResponseDto;
import com.capstone.goat.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Operation(summary = "매칭 시작", description = "url 바디에 {sport,latitude,longitude,matchingStartTime,matchStartTimes,preferCourt,userCount,groupId}을 json형식으로 보내주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "매칭 시작 성공",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404",description = "매칭 등록 실패",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<?> matchingStart(@AuthenticationPrincipal User user, @Valid @RequestBody MatchingConditionDto matchingConditionDto){
        //User user = userRepository.findById(userId).orElseThrow();

        // 사용자가 그룹이 없으면 생성
        if (matchingConditionDto.getGroupId() == null) {
            Group group = groupService.addGroup(user);
            userService.joinGroup(user.getId(), group);
            matchingConditionDto.insertGroupId(group.getId());
        }

        // 그룹원의 평균 rating을 계산
        int rating = ratingService.getRatingMean(matchingConditionDto.getGroupId(), matchingConditionDto.getSport());

        matchMakingService.addMatchingAndMatchMaking(matchingConditionDto, rating);

        matchMakingService.findMatching(matchingConditionDto, rating);

        return new ResponseEntity<>(new ResponseDto(user.getNickname(),"매칭 시작 성공"), HttpStatus.OK);
    }

    /*@Operation(summary = "매칭 중단", description = "매칭 목록에서 사용자를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "매칭 중단 성공",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404",description = "매칭 중단 실패",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @DeleteMapping
    public ResponseEntity<?> matchingRemove(@AuthenticationPrincipal User user){

        return new ResponseEntity<>(new ResponseDto(user,"매칭 중단 성공"), HttpStatus.OK);
    }*/
}
