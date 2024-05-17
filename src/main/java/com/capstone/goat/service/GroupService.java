package com.capstone.goat.service;

import com.capstone.goat.domain.Group;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.response.UserResponseDto;
import com.capstone.goat.exception.ex.CustomErrorCode;
import com.capstone.goat.exception.ex.CustomException;
import com.capstone.goat.repository.GroupRepository;
import com.capstone.goat.repository.NotificationRepository;
import com.capstone.goat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public Group addGroup(long userId) {  // 엔드포인트에서 파라미터로 받아온 엔티티는 더티 체킹이 불가능

        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("해당하는 유저가 존재하지 않습니다."));

        // 그룹장이 user인 그룹 생성
        Group group = Group.builder()
                .masterId(user.getId())
                .build();

        // group에 member로 추가
        group.addMember(user);

        return groupRepository.save(group);
    }

    public List<UserResponseDto> getMembersFromGroup(long groupId) {

        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NoSuchElementException("그룹이 존재하지 않습니다."));

        long masterId = group.getMasterId();
        List<UserResponseDto> memberList = new ArrayList<>();

        group.getMembers().forEach(member -> {
            // UserResponseDto로 변환
            String club = "없음";
            if (member.getClub() != null) {
                club = member.getClub().getName();
            }
            UserResponseDto memberDto = UserResponseDto.of(member, club);

            // memberList에 추가
            if (member.getId().equals(masterId))    // 그룹장이면 맨 앞에 추가
                memberList.add(0, memberDto);
            else    // 맨 뒤에 추가
                memberList.add(memberDto);
        });

        return memberList;
    }

    @Transactional
    public void addInviteeToGroup(long groupId, String inviteeNickname) {

        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NoSuchElementException("그룹이 존재하지 않습니다."));
        User user = userRepository.findByNickname(inviteeNickname).orElseThrow(() -> new NoSuchElementException("해당하는 유저가 존재하지 않습니다."));

        if (user.getGroup() != null)
            throw new CustomException(CustomErrorCode.USER_BELONG_GROUP);

        // 유저가 그룹 초대를 받는 중이면 예외
        for (LocalDateTime sendTime : notificationRepository.findSendTimeByReceiverIdAndType(user.getId(), 2)) {
            if (Duration.between(sendTime, LocalDateTime.now()).getSeconds() <= 30)
                throw new CustomException(CustomErrorCode.USER_INVITED_GROUP);
        }

        // 초대 중인 인원에 추가
        group.addInvitee(user);
        // TODO 알림 기능 추가
    }

    @Transactional
    public void updateInviteeByIsAccepted(long groupId, long userId, boolean isAccepted) {

        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NoSuchElementException("그룹이 존재하지 않습니다."));
        User invitee = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("해당하는 유저가 존재하지 않습니다."));

        if (isAccepted) // 수락이면 그룹에 추가
            group.addMember(invitee);

        group.excludeInvitee(invitee); // 초대 목록에서 삭제
    }

    @Transactional
    public void kickMemberFromGroup(long groupId, long userId) {

        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NoSuchElementException("그룹이 존재하지 않습니다."));
        User member = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("해당하는 유저가 존재하지 않습니다."));

        group.kickMember(member);
    }

    @Transactional
    public void removeMemberFromGroup(long userId) {

        User member = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("해당하는 유저가 존재하지 않습니다."));
        Group group = ofNullable(member.getGroup()).orElseThrow(() -> new NoSuchElementException("가입된 그룹을 찾을 수 없습니다"));

        member.leaveGroup();    // 그룹 탈퇴

        if (Objects.equals(group.getMasterId(), userId)) {    // 그룹장이 탈퇴하는 경우

            if (group.getMembers().size() == 1) {
                group.excludeAllInvitees(); // 초대 중인 유저 목록 제거
                // TODO 알림 구현 시 알림 삭제도 추가
                groupRepository.deleteById(group.getId());  // 그룹 삭제
            } else {
                group.handOverMaster(group.getMembers().get(0).getId());    // 다른 그룹원에게 그룹장 양도
            }
        }

    }


}
