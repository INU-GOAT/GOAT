package com.capstone.goat.controller;

import com.capstone.goat.domain.NotificationType;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.request.GroupAcceptDto;
import com.capstone.goat.dto.request.GroupInviteDto;
import com.capstone.goat.dto.response.GroupResponseDto;
import com.capstone.goat.dto.response.ResponseDto;
import com.capstone.goat.service.GroupService;
import com.capstone.goat.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class GroupController {

    private final GroupService groupService;
    private final NotificationService notificationService;

    /*@Operation(summary = "새로운 그룹 생성", description = "새로운 그룹을 생성합니다.")
    @PostMapping
    public ResponseEntity<?> groupAdd(@Schema(hidden = true) @AuthenticationPrincipal User user){

        if (user.getGroup() != null)
            throw new IllegalStateException("이미 그룹이 생성되어 있습니다");
        long groupId = groupService.addGroup(user.getId());

        return new ResponseEntity<>(new ResponseDto(groupId,"성공"), HttpStatus.OK);
    }*/

    @Operation(summary = "그룹원 조회", description = "그룹원을 조회합니다. 그룹장이 리스트 맨 앞에 위치합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = GroupResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[USER_NOT_FOUND] 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[NO_JOINING_GROUP] 가입된 그룹을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
    })
    @GetMapping
    public ResponseEntity<?> groupList(@Schema(hidden = true) @AuthenticationPrincipal User user){

        log.info("그룹원 조회 id : {}",user.getId());

        GroupResponseDto groupResponseDto = groupService.getMembersFromGroup(user.getId());

        return new ResponseEntity<>(new ResponseDto(groupResponseDto,"성공"), HttpStatus.OK);
    }

    // TODO 알림 추가 하면 완성됨
    @Operation(summary = "그룹에 초대", description = "사용자의 그룹에 새로운 유저를 초대합니다. url 바디에 {inviteeNickname}를 json 형태로 넣어주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "초대 성공, 알림 Id 반환", content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "400", description = "[BAD_REQUEST] 유효성 검사 예외 발생", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[USER_NOT_FOUND] 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[INVITEE_NOT_FOUND] 존재하지 않는 유저입니다. 초대하려는 유저의 닉네임을 다시 확인해주세요.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "[USER_BELONG_GROUP] 그룹에 초대할 수 없습니다. 해당 유저가 이미 그룹에 속해있습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "[USER_INVITED_GROUP] 그룹에 초대할 수 없습니다. 해당 유저가 그룹 초대를 받는 중입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
    })
    @PatchMapping
    public ResponseEntity<?> userInvitedModify(@Schema(hidden = true) @AuthenticationPrincipal User user, @Valid @RequestBody GroupInviteDto groupInviteDto){  // inviteeUserId 하나만 받음

        log.info("[로그] 그룹 초대 메서드 시작");
        log.info("[로그] inviteeUserId : {} ", groupInviteDto.getInviteeNickname());

        long groupId = groupService.addInviteeToGroup(user.getId(), groupInviteDto.getInviteeNickname());
        long notificationId = notificationService.sendNotification(user.getId(), groupInviteDto.getInviteeNickname(), NotificationType.GROUP_INVITE);

        return new ResponseEntity<>(new ResponseDto(notificationId,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "클럽원을 그룹에 초대", description = "사용자의 클럽원들에게 그룹 초대를 보냅니다. 그룹에 가입 가능한 모든 클럽원에게 초대 알림을 보냅니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "초대 성공, 초대된 클럽원 Id 반환", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Long.class)))),
            @ApiResponse(responseCode = "404", description = "[USER_NOT_FOUND] 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[HAS_NOT_CLUB] 가입된 클럽이 없습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "[NO_AVAILABLE_CLUB_MEMBERS] 현재 초대 가능한 클럽원이 없습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),

    })
    @PatchMapping("/club-members")
    public ResponseEntity<?> clubMembersInvitedModify(@Schema(hidden = true) @AuthenticationPrincipal User user) {

        log.info("[로그] 클럽원 그룹 초대 메서드 시작");

        List<Long> inviteeIdList = groupService.addClubMembersToGroupInvitee(user.getId());

        return new ResponseEntity<>(new ResponseDto(inviteeIdList, "성공"), HttpStatus.OK);
    }

    /*@Operation(summary = "그룹장 조회", description = "그룹장을 조회합니다.")
    @GetMapping("{groupId}/master")
    public ResponseEntity<?> groupMasterDetails(@PathVariable Integer groupId){

        return new ResponseEntity<>(new ResponseDto(null,"성공"), HttpStatus.OK);
    }*/

    @Operation(summary = "초대 수락에 따른 그룹원 추가", description = "그룹장의 초대를 수락하면 그룹에 추가합니다. url 바디에 {sendTime, isAccepted}를 json 형태로 넣어주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "그룹원 추가 성공, 그룹 Id 반환", content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "400", description = "[BAD_REQUEST] 유효성 검사 예외 발생", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "[GROUP_INVITE_TIME_OVER] 초대 유효 시간이 지났습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[USER_NOT_FOUND] 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[NO_JOINING_GROUP] 가입된 그룹을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[NOTIFICATION_NOT_FOUND] 존재하지 않는 알림입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
    })
    @PatchMapping("/members")
    public ResponseEntity<?> groupUpdate(@Schema(hidden = true) @AuthenticationPrincipal User user, @Valid @RequestBody GroupAcceptDto groupAcceptDto){ // isAccepted와 sendTime 받음

        log.info("초대 수락에 따른 그룹원 추가 id : {}, notificationId : {}, isAccepted : {}", user.getId(), groupAcceptDto.getNotificationId(), groupAcceptDto.getIsAccepted());

        long groupId = groupService.updateInvitee(user.getId(), groupAcceptDto);

        return new ResponseEntity<>(new ResponseDto(groupId, "성공"), HttpStatus.OK);
    }

    @Operation(summary = "그룹 추방", description = "그룹에서 그룹원을 추방시킵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추방 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[USER_NOT_FOUND] 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[NO_JOINING_GROUP] 가입된 그룹을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
    })
    @PatchMapping("/members/{memberId}")
    public ResponseEntity<?> groupMembersRemove(@Schema(hidden = true) @AuthenticationPrincipal User user, @PathVariable Long memberId) {

        log.info("그룹 추방 id : {}",memberId);

        groupService.kickMemberFromGroup(user.getId(), memberId);

        return new ResponseEntity<>(new ResponseDto<>(null, "성공"), HttpStatus.OK);
    }

    @Operation(summary = "그룹 탈퇴", description = "그룹을 탈퇴합니다. 그룹장이 탈퇴 시 그룹장을 양도 후 탈퇴합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "탈퇴 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[USER_NOT_FOUND] 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "[NO_JOINING_GROUP] 가입된 그룹을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
    })
    @DeleteMapping
    public ResponseEntity<?> groupRemove(@Schema(hidden = true) @AuthenticationPrincipal User user){

        log.info("그룹 탈퇴 id : {}",user.getId());

        groupService.leaveGroup(user.getId());

        return new ResponseEntity<>(new ResponseDto(null,"성공"), HttpStatus.OK);
    }

}
