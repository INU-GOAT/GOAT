package com.capstone.goat.controller;

import com.capstone.goat.domain.Sport;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.response.RatingResponseDto;
import com.capstone.goat.dto.response.ResponseDto;
import com.capstone.goat.service.RatingService;
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

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/rating")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class RatingController {

    private final RatingService ratingService;

    @Operation(summary = "레이팅 목록 조회", description = "사용자의 레이팅을 모두 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RatingResponseDto.class)))),
            @ApiResponse(responseCode = "404", description = "[USER_NOT_FOUND] 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
    })
    @GetMapping
    public ResponseEntity<?> ratingDetails(@Schema(hidden = true) @AuthenticationPrincipal User user) {

        log.info("레이팅 목록 조회가 id : {}",user.getId());

        List<RatingResponseDto> ratingResponseDtoList = ratingService.getRatingList(user.getId());

        return new ResponseEntity<>(new ResponseDto<>(ratingResponseDtoList,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "레이팅 상세 조회", description = "사용자 레이팅 중 하나를 상세 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "조회 성공",content = @Content(schema = @Schema(implementation = RatingResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[USER_NOT_FOUND] 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
    })
    @GetMapping("/{sportName}")
    public ResponseEntity<?> ratingBySport(@Schema(hidden = true) @AuthenticationPrincipal User user, @PathVariable String sportName) {

        log.info("레이팅 상세 조회 id : {}",user.getId());

        RatingResponseDto ratingResponseDto = ratingService.getRatingBySport(user.getId(), sportName);

        return new ResponseEntity<>(new ResponseDto<>(ratingResponseDto,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "레이팅 랜덤 생성", description = "사용자에 레이팅을 랜덤한 점수로 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "생성 성공",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
    })
    @PostMapping
    public ResponseEntity<?> ratingAdd(@Schema(hidden = true) @AuthenticationPrincipal User user) {

        Random random = new Random(); //랜덤 객체 생성(디폴트 시드값 : 현재시간)
        random.setSeed(System.currentTimeMillis()); //시드값 설정을 따로 할수도 있음

        ratingService.initRating(user.getId(), Sport.BASKETBALL, random.nextInt(60));
        ratingService.initRating(user.getId(), Sport.SOCCER, random.nextInt(60));
        ratingService.initRating(user.getId(), Sport.BADMINTON, random.nextInt(60));
        ratingService.initRating(user.getId(), Sport.TABLE_TENNIS, random.nextInt(60));

        return new ResponseEntity<>(new ResponseDto<>(null,"성공"), HttpStatus.CREATED);
    }

}
