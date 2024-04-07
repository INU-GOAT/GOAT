package com.capstone.goat.config;

import com.capstone.goat.domain.Chat;
import com.capstone.goat.repository.ChatRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper;
    private final ChatRepository chatRepository;
    private final Set<WebSocketSession> socketSessions =new HashSet<>();
    private final Map<Long,Set<WebSocketSession>> chatRoomSessionMap = new HashMap<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("{} 연결됨",session.getId());
        socketSessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload {}",payload);

        Chat chat = mapper.readValue(payload, Chat.class);

        Long chatRoomId = chat.getGameId();
        if(!chatRoomSessionMap.containsKey(chatRoomId)){
            chatRoomSessionMap.put(chatRoomId,new HashSet<>());
        }
        Set<WebSocketSession> chatRoomSession = chatRoomSessionMap.get(chatRoomId);
        if(chat.getMessageType().equals(Chat.messageType.ENTER)){
            chatRoomSession.add(session);
            chat.changeMessage(chat.getUserNickname()+"님이 입장하셨습니다.");
        }
        if(chat.getMessageType().equals(Chat.messageType.QUIT)){
            chatRoomSession.remove(session);
            chat.changeMessage(chat.getUserNickname()+"님이 퇴장하셨습니다.");
        }
        chatRoomSession.parallelStream().forEach(ses->sendMessage(ses,chat));
        chatRepository.save(chat);
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("{} 연결 끊김", session.getId());
        socketSessions.remove(session);
    }

    public <T> void sendMessage(WebSocketSession session, T message){
        try {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }
}
