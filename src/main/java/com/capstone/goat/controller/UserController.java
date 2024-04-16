package com.capstone.goat.controller;

import com.capstone.goat.domain.User;
import com.capstone.goat.dto.response.TokenDto;
import com.capstone.goat.dto.request.UserUpdateDto;
import com.capstone.goat.dto.response.ResponseDto;
import com.capstone.goat.dto.response.UserResponseDto;
import com.capstone.goat.dto.request.UserSaveDto;
import com.capstone.goat.service.UserService;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
        private final UserService userService;
    @Operation(summary = "회원가입", description = "url 헤더에 토큰을, 바디에 {age,gender,prefer_sport, soccer_tier,basketball_tier,badminton_tier}을 json형식으로 보내주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "201",description = "회원가입성공",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400",description = "이미 존재하는 닉네임입니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404",description = "존재하지 않는 유저입니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PostMapping("")
    public ResponseEntity<ResponseDto<Long>> save(@Valid@RequestBody UserSaveDto userSaveDto,@Schema(hidden = true)@AuthenticationPrincipal User user){
        log.info("회원가입 호출 id={}",user.getId());
        return new ResponseEntity<>(new ResponseDto<>(userService.join(user.getId(), userSaveDto), "회원가입성공"), HttpStatus.CREATED);
    }

    @Operation(summary = "회원정보수정", description = "url 헤더에 토큰을, 바디에 {nickname, age,gender,prefer_sport, soccer_tier,basketball_tier,badminton_tier}을 json형식으로 보내주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "회원정보수정성공",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400",description = "이미 존재하는 닉네임입니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404",description = "존재하지 않는 유저입니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PutMapping("")
    public ResponseEntity<ResponseDto<Long>> update(@Valid@RequestBody UserUpdateDto userUpdateDto, @Schema(hidden = true)@AuthenticationPrincipal User user){
        log.info("회원정보수정 호출 id={}",user.getId());
        return new ResponseEntity<>(new ResponseDto<>(userService.update(user.getId(),userUpdateDto), "회원정보수정성공"), HttpStatus.OK);
    }

    @Operation(summary = "회원탈퇴", description = "url 헤더에 토큰을 보내주세요")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "회원탈퇴성공",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @DeleteMapping("")
    public ResponseEntity<ResponseDto<Long>> delete(@Schema(hidden = true)@AuthenticationPrincipal User user){
        log.info("회원탈퇴 호출 id={}",user.getId());
        userService.delete(user);
        return new ResponseEntity<>(new ResponseDto<>(1L, "회원탈퇴성공"), HttpStatus.OK);
    }

    @Operation(summary = "카카오 로그인(코드방식)",description = "헤더에 카카오 인증으로 얻은 코드를 보내주세요")
    @PostMapping("/code")
    public ResponseEntity<ResponseDto<TokenDto>> getUserByCode(HttpServletRequest httpServletRequest) {
        String userCode = httpServletRequest.getHeader("code");
        return new ResponseEntity<>(new ResponseDto<>(userService.OAuthLogin(userCode),"로그인 성공"), HttpStatus.OK);
    }

    @Operation(summary = "카카오 로그인(토큰방식)",description = "헤더에 카카오 인증으로 얻은 토큰을 보내주세요")
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<TokenDto>> getUser(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("kakao");
        return new ResponseEntity<>(new ResponseDto<>(userService.LoginByToken(token),"로그인 성공"), HttpStatus.OK);
    }

    @Operation(summary = "토큰 재발급",description = "헤더에 refresh 토큰을 보내주세요. 토큰 유효시간은 2시간, 리프레시 토큰의 유효시간은 1일입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "토큰 재발급 성공",content = @Content(schema = @Schema(implementation = TokenDto.class)))
            ,@ApiResponse(responseCode = "401",description = "만료된 토큰입니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
            ,@ApiResponse(responseCode = "404",description = "존재하지 않는 회원입니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<ResponseDto<TokenDto>> refresh(HttpServletRequest httpServletRequest){
        log.info("토큰 재발급 호출");
        return new ResponseEntity<>(new ResponseDto<>(userService.refreshToken(httpServletRequest.getHeader("refresh")),"토큰 재발급 성공"),HttpStatus.OK);
    }

    @Operation(summary = "유저 정보 가져오기", description = "url 파라미터에 유저의 데이터베이스 id 값을 보내주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "유저 정보 가져오기성공",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400",description = "잘못된 입력",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @GetMapping("")
    public ResponseEntity<ResponseDto<UserResponseDto>> getUser(@Schema(hidden = true)@AuthenticationPrincipal User user){
        return new ResponseEntity<>(new ResponseDto<>(userService.getUser(user),"유저 정보 가져오기성공"), HttpStatus.OK);
    }

    @Operation(summary = "클럽 탈퇴")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "클럽 탈퇴 성공",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400",description = "가입된 클럽이 없습니다. / 클럽장은 탈퇴가 불가능합니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PutMapping("/club")
    public ResponseEntity<ResponseDto<Long>> outClub(@Schema(hidden = true)@AuthenticationPrincipal User user){
        userService.outClub(user.getId());
        return new ResponseEntity<>(new ResponseDto<>(1L,"클럽 탈퇴 성공"), HttpStatus.OK);
    }

    @Operation(summary = "더미데이터 유저 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "더미데이터 생성 완료",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400",description = "이미 존재하는 id 값 입니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PostMapping("/dummy/{id}")
    public ResponseEntity<ResponseDto<Long>> createDummy (@PathVariable Long id){
        return new ResponseEntity<>(new ResponseDto<>(userService.createDummyUser(id),"더미데이터 생성 완료"),HttpStatus.OK);

    }

    @Operation(summary = "더미데이터 유저 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "더미유저 로그인 완료",content = @Content(schema = @Schema(implementation = TokenDto.class))),
            @ApiResponse(responseCode = "404",description = "존재하지 않는 유저입니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PostMapping("/dummy/{id}/login")
    public ResponseEntity<ResponseDto<TokenDto>> loginDummy (@PathVariable Long id){
        return new ResponseEntity<>(new ResponseDto<>(userService.loginDummy(id),"더미유저 로그인 완료"),HttpStatus.OK);

    }



}
