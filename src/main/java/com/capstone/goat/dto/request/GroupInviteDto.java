package com.capstone.goat.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupInviteDto {

    @NotNull(message = "InviteeUserId는 비어있을 수 없습니다.")
    private Long inviteeUserId;
}
