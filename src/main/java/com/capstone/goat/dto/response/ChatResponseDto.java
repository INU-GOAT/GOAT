package com.capstone.goat.dto.response;


import com.capstone.goat.domain.Chat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatResponseDto {
    @Schema(description = "대화 내용")
    private String comment;
    @Schema(description = "시간")
    private LocalDateTime time;
    @Schema(description = "채팅 한 사람 닉네임")
    private String userNickname;

    @Builder
    private ChatResponseDto(String comment, LocalDateTime time, String userNickname){
        this.comment = comment;
        this.time = time;
        this.userNickname = userNickname;
    }

    public static ChatResponseDto of(Chat chat){
        return ChatResponseDto.builder().comment(chat.getComment()).time(chat.getTime()).userNickname(chat.getUserNickname()).build();
    }
}
