package com.capstone.goat.controller;

import com.capstone.goat.domain.Group;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.request.GroupAcceptDto;
import com.capstone.goat.dto.request.GroupInviteDto;
import com.capstone.goat.dto.response.ResponseDto;
import com.capstone.goat.dto.response.UserResponseDto;
import com.capstone.goat.exception.ex.CustomErrorCode;
import com.capstone.goat.exception.ex.CustomException;
import com.capstone.goat.repository.UserRepository;
import com.capstone.goat.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static java.util.Optional.ofNullable;

@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class GroupController {

    private final GroupService groupService;
    private final UserRepository userRepository;

    /*@Operation(summary = "새로운 그룹 생성", description = "새로운 그룹을 생성합니다.")
    @PostMapping
    public ResponseEntity<?> groupAdd(@Schema(hidden = true) @AuthenticationPrincipal User user){

        if (user.getGroup() != null)
            throw new IllegalStateException("이미 그룹이 생성되어 있습니다");
        long groupId = groupService.addGroup(user.getId());

        return new ResponseEntity<>(new ResponseDto(groupId,"성공"), HttpStatus.OK);
    }*/

    @Operation(summary = "그룹원 조회", description = "그룹원을 조회합니다. 그룹장이 리스트 맨 앞에 위치합니다.")
    @GetMapping
    public ResponseEntity<?> groupList(@Schema(hidden = true) @AuthenticationPrincipal User user){

        log.info("그룹원 조회 id : {}",user.getId());

        List<UserResponseDto> memberDtoList = groupService.getMembersFromGroup(user.getId());

        return new ResponseEntity<>(new ResponseDto(memberDtoList,"성공"), HttpStatus.OK);
    }

    // TODO 알림 추가 하면 완성됨
    @Operation(summary = "그룹에 초대", description = "사용자의 그룹에 새로운 유저를 초대합니다. url 바디에 {inviteeUserId}를 json 형태로 넣어주세요.")
    @PatchMapping
    public ResponseEntity<?> userInvitedModify(@Schema(hidden = true) @AuthenticationPrincipal User user, @Valid @RequestBody GroupInviteDto groupInviteDto){  // inviteeUserId 하나만 받음

        log.info("[로그] 그룹 초대 메서드 시작");
        log.info("[로그] inviteeUserId : {} ", groupInviteDto.getInviteeUserId());

        long groupId = groupService.addInviteeToGroup(user.getId(), groupInviteDto.getInviteeUserId());

        return new ResponseEntity<>(new ResponseDto(groupId,"성공"), HttpStatus.OK);
    }

    /*@Operation(summary = "그룹장 조회", description = "그룹장을 조회합니다.")
    @GetMapping("{groupId}/master")
    public ResponseEntity<?> groupMasterDetails(@PathVariable Integer groupId){

        return new ResponseEntity<>(new ResponseDto(null,"성공"), HttpStatus.OK);
    }*/

    @Operation(summary = "초대 수락에 따른 그룹원 추가", description = "그룹장의 초대를 수락하면 그룹에 추가합니다. url 바디에 {sendTime, isAccepted}를 json 형태로 넣어주세요.")
    @PatchMapping("/members")
    public ResponseEntity<?> groupUpdate(@Schema(hidden = true) @AuthenticationPrincipal User user, @Valid @RequestBody GroupAcceptDto groupAcceptDto){ // isAccepted와 sendTime 받음

        log.info("초대 수락에 따른 그룹원 추가 id : {}, sendTime : {}, isAccepted : {}", user.getId(), groupAcceptDto.getSendTime(), groupAcceptDto.getIsAccepted());

        long groupId = groupService.updateInvitee(user.getId(), groupAcceptDto);

        return new ResponseEntity<>(new ResponseDto(groupId, "성공"), HttpStatus.OK);
    }

    @Operation(summary = "그룹 추방", description = "그룹에서 그룹원을 추방시킵니다.")
    @PatchMapping("/members/{memberId}")
    public ResponseEntity<?> groupMembersRemove(@Schema(hidden = true) @AuthenticationPrincipal User user, @PathVariable Long memberId) {

        log.info("그룹 추방 id : {}",memberId);

        user = userRepository.findById(user.getId()).orElseThrow();

        // 그룹이 존재하지 않을 경우 예외
        Group group = ofNullable(user.getGroup())
                .orElseThrow(() -> new CustomException(CustomErrorCode.NO_JOINING_GROUP));

        groupService.kickMemberFromGroup(user.getId(), memberId);

        return new ResponseEntity<>(new ResponseDto<>(null, "성공"), HttpStatus.OK);
    }

    @Operation(summary = "그룹 탈퇴", description = "그룹을 탈퇴합니다. 그룹장이 탈퇴 시 그룹장을 양도 후 탈퇴합니다.")
    @DeleteMapping
    public ResponseEntity<?> groupRemove(@Schema(hidden = true) @AuthenticationPrincipal User user){

        log.info("그룹 탈퇴 id : {}",user.getId());

        groupService.removeUserFromGroup(user.getId());

        return new ResponseEntity<>(new ResponseDto(null,"성공"), HttpStatus.OK);
    }

}
