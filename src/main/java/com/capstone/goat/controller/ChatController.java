package com.capstone.goat.controller;

import com.capstone.goat.dto.response.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {
    /*@Operation(summary = "메시지 전송", description = "사용자가 작성한 메시지를 전송합니다. url 바디에 {comment,time,gameId}를 담아 json 형태로 보내주세요.")
    @PostMapping()
    public ResponseEntity<?> chatAdd(){
        return new ResponseEntity<>(new ResponseDto(null,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "대화 목록 조회", description = "해당 채팅방의 대화 목록을 조회합니다. url 바디에 {gameId}를 담아 json 형태로 보내주세요.")
    @GetMapping()
    public ResponseEntity<?> chatList(Integer gameId){
        return new ResponseEntity<>(new ResponseDto(null,"성공"), HttpStatus.OK);
    }*/
}
