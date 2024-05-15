package com.capstone.goat.controller;

import com.capstone.goat.domain.Group;
import com.capstone.goat.domain.User;
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

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class GroupController {

    private final GroupService groupService;
    private final UserRepository userRepository;

    // TODO 만약 그룹원 초대 시 자동 생성이라면 필요 없음
    /*@Operation(summary = "새로운 그룹 생성", description = "새로운 그룹을 생성합니다.")
    @PostMapping
    public ResponseEntity<?> groupAdd(@Schema(hidden = true) @AuthenticationPrincipal User user){

        if (user.getGroup() != null)
            throw new IllegalStateException("이미 그룹이 생성되어 있습니다");
        long groupId = groupService.addGroup(user.getId());

        return new ResponseEntity<>(new ResponseDto(groupId,"성공"), HttpStatus.OK);
    }*/

    @Operation(summary = "그룹원 조회", description = "그룹원을 조회합니다. 그룹장이 리스트 맨 앞에 위치합니다. 그룹에 속해있지 않을 경우 null이 반환됩니다.")
    @GetMapping
    public ResponseEntity<?> groupList(@Schema(hidden = true) @AuthenticationPrincipal User user){
        log.info("그룹원 조회 id : {}",user.getId());
        user = userRepository.findById(user.getId()).orElseThrow();

        Group group = user.getGroup();
        List<UserResponseDto> memberDtoList = null;

        // TODO 반환 형태 적합한 지 -> 아마 rating 도 포함해서 반환하려면 새로 dto를 만드는게 좋을 듯
        if (group != null) {
             memberDtoList = groupService.getMembersFromGroup(group.getId());
        }

        return new ResponseEntity<>(new ResponseDto(memberDtoList,"성공"), HttpStatus.OK);
    }

    // TODO 알림 추가 하면 완성됨
    @Operation(summary = "그룹에 초대", description = "사용자의 그룹에 새로운 유저를 초대합니다. url 바디에 {inviteeNickname}를 json 형태로 넣어주세요.")
    @PatchMapping
    public ResponseEntity<?> userInvitedModify(@Schema(hidden = true) @AuthenticationPrincipal User user, @RequestBody Map<String, String> param){  // inviteeNickname 하나만 받음

        log.info("[로그] 그룹 초대 메서드 시작");

        // 파라미터 검증
        String inviteeNickname = param.get("inviteeNickname");
        log.info("그룹에 초대 조회 초대된 사람: {}",inviteeNickname);
        if (inviteeNickname == null) throw new IllegalArgumentException("inviteeNickname의 형식이 잘못되었습니다.");

        log.info("[로그] inviteeNickname = " + inviteeNickname);

        long userId = user.getId();
        user = userRepository.findById(userId).orElseThrow();

        Group group = ofNullable(user.getGroup())
                .orElseGet(() -> groupService.addGroup(userId));  // 사용자에게 그룹이 없으면 그룹 생성

        log.info("[로그] group : " + group);

        groupService.addInviteeToGroup(group.getId(), inviteeNickname);

        return new ResponseEntity<>(new ResponseDto(group.getId(),"성공"), HttpStatus.OK);
    }

    // 필요한 지 모르겠음
    /*@Operation(summary = "그룹장 조회", description = "그룹장을 조회합니다.")
    @GetMapping("{groupId}/master")
    public ResponseEntity<?> groupMasterDetails(@PathVariable Integer groupId){

        return new ResponseEntity<>(new ResponseDto(null,"성공"), HttpStatus.OK);
    }*/

    @Operation(summary = "초대 수락에 따른 그룹원 추가", description = "그룹장의 초대를 수락하면 그룹에 추가합니다. url 바디에 {sendTime, isAccepted}를 json 형태로 넣어주세요.")
    @PatchMapping("/{groupId}")
    public ResponseEntity<?> groupSave(@Schema(hidden = true) @AuthenticationPrincipal User user, @RequestBody Map<String, String> param){ // isAccepted와 sendTime 받음
        log.info("초대 수락에 따른 그룹원 추가 id : {}",user.getId());
        // 파라미터 검증
        LocalDateTime sendTime;
        try {
            sendTime = LocalDateTime.parse(param.get("sendTime"));
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("sendTime 형식이 잘못되었습니다. LocalDateTime 형식이어야 합니다.");
        }

        boolean isAccepted;
        String isAcceptedString = param.get("isAccepted");
        if ("true".equalsIgnoreCase(isAcceptedString)) {
            isAccepted = true;
        } else if ("false".equalsIgnoreCase(isAcceptedString)) {
            isAccepted = false;
        } else {
            throw new IllegalArgumentException("isAccepted 형식이 잘못되었습니다. 'true' 또는 'false' 문자열이 와야 합니다.");
        }

        String msg = "성공";
        HttpStatus httpStatus = HttpStatus.OK;
        // 만약 초대를 수락했는데 초대 메시지를 받은 지 30초가 지난 상태라면 초대 거절
        /*if (isAccepted && Duration.between(sendTime, LocalDateTime.now()).getSeconds() > 30) {
            isAccepted = false;
            msg = "초대 유효 시간이 지났습니다.";
            httpStatus = HttpStatus.BAD_REQUEST;
        }*/

        long groupId = user.getInvitedGroup().getId();  // TODO 초대 받은 group이 null인 경우도 예외 처리 해야하나

        groupService.updateInviteeByIsAccepted(groupId, user.getId(), isAccepted);

        return new ResponseEntity<>(new ResponseDto(groupId, msg), httpStatus);
    }

    @Operation(summary = "그룹 추방", description = "그룹에서 그룹원을 추방시킵니다.")
    @PatchMapping("/members/{memberId}")
    public ResponseEntity<?> groupMembersRemove(@Schema(hidden = true) @AuthenticationPrincipal User user, @PathVariable Long memberId) {
        log.info("그룹 추방 id : {}",memberId);
        user = userRepository.findById(user.getId()).orElseThrow();

        // 그룹이 존재하지 않을 경우 예외
        Group group = ofNullable(user.getGroup())
                .orElseThrow(() -> new CustomException(CustomErrorCode.NO_JOINING_GROUP));

        groupService.kickMemberFromGroup(group.getId(), memberId);

        return new ResponseEntity<>(new ResponseDto<>(null, "성공"), HttpStatus.OK);
    }

    @Operation(summary = "그룹 탈퇴", description = "그룹을 탈퇴합니다. 그룹장이 탈퇴 시 그룹을 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<?> groupRemove(@Schema(hidden = true) @AuthenticationPrincipal User user){
        log.info("그룹 탈퇴 id : {}",user.getId());
        groupService.removeMemberFromGroup(user.getId());

        return new ResponseEntity<>(new ResponseDto(null,"성공"), HttpStatus.OK);
    }

}
