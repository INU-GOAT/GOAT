package com.capstone.goat.controller;

import com.capstone.goat.domain.Chat;
import com.capstone.goat.dto.request.ChatDto;
import com.capstone.goat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class ChatController {
    private final SimpMessagingTemplate template;
    private final ChatRepository chatRepository;

    @MessageMapping("/enter/{gameId}")
    public void enter(@DestinationVariable Long gameId, ChatDto chatDto){
        log.info("입장 : {}",chatDto.getUserNickname());
        template.convertAndSend("/room/"+gameId,chatDto.getUserNickname()+"님이 입장하셨습니다.");
    }

    @MessageMapping("/message/{gameId}")
    public void message(@DestinationVariable Long gameId, ChatDto chatDto){
        log.info("메시지 보냄");
        chatRepository.save(Chat.builder().gameId(gameId).comment(chatDto.getComment()).userNickname(chatDto.getUserNickname()).build());
        template.convertAndSend("/room/"+gameId,chatDto.getComment());
    }

}
