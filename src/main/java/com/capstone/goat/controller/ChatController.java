package com.capstone.goat.controller;

import com.capstone.goat.domain.Chat;
import com.capstone.goat.dto.request.ChatDto;
import com.capstone.goat.dto.response.ChatResponseDto;
import com.capstone.goat.dto.response.ResponseDto;
import com.capstone.goat.repository.ChatRepository;
import com.capstone.goat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class ChatController {
    private final SimpMessagingTemplate template;
    private final ChatService chatService;


    @MessageMapping("/enter/{gameId}")
    public void enter(@DestinationVariable Long gameId, ChatDto chatDto){
        log.info("입장 : {}",chatDto.getUserNickname());
        template.convertAndSend("/room/"+gameId,chatDto.getUserNickname()+"님이 입장하셨습니다.");
    }

    @MessageMapping("/message/{gameId}")
    public void message(@DestinationVariable Long gameId, ChatDto chatDto){
        log.info("메시지 보냄");
        Chat chat = chatService.saveChat(gameId,chatDto);
        template.convertAndSend("/room/"+gameId,chat);
    }

    @ResponseBody
    @GetMapping("/api/chats/{gameId}")
    @Operation(summary = "채팅 기록 가져오기", description = "url 변수에 gameId를 보내주세요")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "채팅 기록 가져오기 성공",content = @Content(schema = @Schema(implementation = ChatResponseDto.class))),
    })
    public ResponseEntity<ResponseDto<List<ChatResponseDto>>> getChatList(@PathVariable Long gameId){
        return new ResponseEntity<>(new ResponseDto<>(chatService.getChatList(gameId),"채팅 기록 가져오기 성공"), HttpStatus.OK);
    }

}
