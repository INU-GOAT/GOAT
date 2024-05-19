package com.capstone.goat.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupAcceptDto {

    @NotNull(message = "notificationId는 비어있을 수 없습니다.")
    private Long notificationId;

    @NotNull(message = "isAccepted는 비어있을 수 없습니다.")
    private Boolean isAccepted;
}
