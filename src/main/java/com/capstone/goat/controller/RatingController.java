package com.capstone.goat.controller;

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

}
