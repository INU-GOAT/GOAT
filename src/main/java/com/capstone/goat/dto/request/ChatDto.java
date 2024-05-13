package com.capstone.goat.dto.request;

import com.capstone.goat.domain.Chat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    @Schema(description = "채팅 작성자 닉네임")
    private String userNickname;
    @Schema(description = "채팅 내용")
    private String comment;

}
