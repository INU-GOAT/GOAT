package com.capstone.goat.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GroupResponseDto {

    private final Long clubId;

    private final Long groupMasterId;

    private final List<String> members;

    @Builder(access = AccessLevel.PRIVATE)
    private GroupResponseDto(Long clubId, Long groupMasterId, List<String> members) {
        this.clubId = clubId;
        this.groupMasterId = groupMasterId;
        this.members = members;
    }


    public static GroupResponseDto of(Long clubId, Long groupMasterId, List<String> members) {
        return GroupResponseDto.builder()
                .clubId(clubId)
                .groupMasterId(groupMasterId)
                .members(members)
                .build();
    }
}
