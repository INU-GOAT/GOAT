package com.capstone.goat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/group/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GroupController {

    /*@Operation(summary = "새로운 그룹 생성", description = "새로운 그룹을 생성합니다.")
    @PostMapping()
    public ResponseEntity<?> groupAdd(){
        return new ResponseEntity<>(new ResponseDto(null,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "그룹원 조회", description = "사용자의 그룹원을 조회합니다.")
    @GetMapping()
    public ResponseEntity<?> groupList(@AuthenticationPrincipal User user){
        user.getGroup().getMembers();
        return new ResponseEntity<>(new ResponseDto(null,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "그룹에 초대", description = "사용자의 그룹에 새로운 유저를 초대합니다. url 바디에 {inviteeNickname}를 json 형태로 넣어주세요.")
    @PutMapping()
    public ResponseEntity<?> userInvitedModify(@AuthenticationPrincipal User user, String inviteeNickname){
        return new ResponseEntity<>(new ResponseDto(null,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "그룹장 조회", description = "그룹장을 조회합니다.")
    @GetMapping("{groupId}/master")
    public ResponseEntity<?> groupMasterDetails(@PathVariable Integer groupId){
        return new ResponseEntity<>(new ResponseDto(null,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "초대 수락에 따른 그룹원 추가", description = "그룹장의 초대를 수락하면 그룹에 추가합니다. url 바디에 {isAccepted}를 json 형태로 넣어주세요.")
    @PutMapping("{groupId}")
    public ResponseEntity<?> groupSave(@AuthenticationPrincipal User user){
        return new ResponseEntity<>(new ResponseDto(null,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "그룹 삭제", description = "그룹장의 그룹 해체 또는 게임 시작으로 그룹을 삭제합니다.")
    @DeleteMapping("{groupId}")
    public ResponseEntity<?> groupRemove(){
        return new ResponseEntity<>(new ResponseDto(null,"성공"), HttpStatus.OK);
    }*/

}
