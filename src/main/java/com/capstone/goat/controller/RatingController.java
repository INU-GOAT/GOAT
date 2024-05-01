package com.capstone.goat.controller;

import com.capstone.goat.domain.Sport;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.response.RatingResponseDto;
import com.capstone.goat.dto.response.ResponseDto;
import com.capstone.goat.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/rating/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RatingController {

    private final RatingService ratingService;

    @Operation(summary = "레이팅 목록 조회", description = "사용자의 레이팅을 모두 조회합니다.")
    @GetMapping
    public ResponseEntity<?> ratingDetails(@Schema(hidden = true) @AuthenticationPrincipal User user) {

        List<RatingResponseDto> ratingResponseDtoList = ratingService.getRatingList(user.getId());

        return new ResponseEntity<>(new ResponseDto<>(ratingResponseDtoList,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "레이팅 상세 조회", description = "사용자 레이팅 중 하나를 상세 조회합니다.")
    @GetMapping("{sportName}")  // sportName으로 받는 상황이 나올 지 ratingId로 받는 상황이 나올지 아직 모르겠음
    public ResponseEntity<?> ratingBySport(@Schema(hidden = true) @AuthenticationPrincipal User user, @PathVariable String sportName) {

        RatingResponseDto ratingResponseDto = ratingService.getRatingBySport(user.getId(), sportName);

        return new ResponseEntity<>(new ResponseDto<>(ratingResponseDto,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "레이팅 랜덤 생성", description = "사용자에 레이팅을 랜덤한 점수로 추가합니다.")
    @PostMapping
    public ResponseEntity<?> ratingAdd(@Schema(hidden = true) @AuthenticationPrincipal User user) {

        Random random = new Random(); //랜덤 객체 생성(디폴트 시드값 : 현재시간)
        random.setSeed(System.currentTimeMillis()); //시드값 설정을 따로 할수도 있음

        ratingService.initRating(user.getId(), Sport.BASKETBALL, random.nextInt(60));
        ratingService.initRating(user.getId(), Sport.SOCCER, random.nextInt(60));
        ratingService.initRating(user.getId(), Sport.BADMINTON, random.nextInt(60));
        ratingService.initRating(user.getId(), Sport.TABLE_TENNIS, random.nextInt(60));

        return new ResponseEntity<>(new ResponseDto<>(null,"성공"), HttpStatus.OK);
    }

}
