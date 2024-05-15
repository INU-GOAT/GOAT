package com.capstone.goat.service;

import com.capstone.goat.domain.Chat;
import com.capstone.goat.dto.request.ChatDto;
import com.capstone.goat.dto.response.ChatResponseDto;
import com.capstone.goat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    @Transactional
    public void saveChat(Long gameId, ChatDto chatDto){
        chatRepository.save(Chat.builder().gameId(gameId).comment(chatDto.getComment()).userNickname(chatDto.getUserNickname()).build());
    }

    @Transactional(readOnly = true)
    public List<ChatResponseDto> getChatList(Long gameId){
        return chatRepository.findAllByGameId(gameId).stream().map(ChatResponseDto::of).collect(Collectors.toList());
    }
}
