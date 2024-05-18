package com.capstone.goat.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupAcceptDto {

    @NotNull(message = "isAccepted는 비어있을 수 없습니다.")
    private Boolean isAccepted;

    @NotNull(message = "sendTime은 비어있을 수 없습니다.")
    private LocalDateTime sendTime;
}
