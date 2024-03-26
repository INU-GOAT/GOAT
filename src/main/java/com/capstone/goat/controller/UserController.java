package com.capstone.goat.controller;

import com.capstone.goat.dto.ResponseDto;
import com.capstone.goat.dto.UserResponseDto;
import com.capstone.goat.dto.UserSaveDto;
import com.capstone.goat.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/")
@CrossOrigin(origins = "*")
public class UserController {
        private final UserService userService;
    @ApiOperation(value = "회원가입", notes = "바디에 {name,phone,loing_id,password,age,soccer_career,basketball_career,badminton_career,soccer_position,basketball_position}을 json형식으로 보내주세요. 경력이나 포지션을 입력하지 않았을경우 값에 -1을 담아 보내주세요")
    @ApiResponses({
            @ApiResponse(code = 200,message = "회원가입성공"),
            @ApiResponse(code = 400,message = "잘못된 입력")
    })
    @PostMapping("")
    public ResponseEntity<ResponseDto<Long>> save(@Valid@RequestBody UserSaveDto userSaveDto){
        Long id = userService.save(userSaveDto);
        log.info("유저 save 호출 id={}",id);
        return new ResponseEntity<>(new ResponseDto(id,"회원가입성공"), HttpStatus.OK);
    }

    @ApiOperation(value = "유저 정보 가져오기", notes = "url 파라미터에 유저의 데이터베이스 id 값을 보내주세요.")
    @ApiResponses({
            @ApiResponse(code = 200,message = "회원가입성공"),
            @ApiResponse(code = 400,message = "잘못된 입력")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<UserResponseDto>> getUser(@PathVariable("id") Long id){
        return new ResponseEntity<>(new ResponseDto(userService.getUser(id),"회원가입성공"), HttpStatus.OK);
    }



}
