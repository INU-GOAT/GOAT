package com.capstone.goat.service;

import com.capstone.goat.domain.*;
import com.capstone.goat.dto.request.GroupAcceptDto;
import com.capstone.goat.dto.response.GroupResponseDto;
import com.capstone.goat.exception.ex.CustomErrorCode;
import com.capstone.goat.exception.ex.CustomException;
import com.capstone.goat.repository.GroupRepository;
import com.capstone.goat.repository.NotificationRepository;
import com.capstone.goat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @Transactional
    public Group getGroup(long userId, boolean isClubGroup) {  // 사용자에게 그룹이 없으면 생성해서 반환

        User user = getUser(userId);

        return Optional.ofNullable(user.getGroup())
                .orElseGet(() -> {
                    // 클럽 그룹일 시, 사용자의 클럽 id를 가져옴
                    Long clubId = isClubGroup
                            ? Optional.ofNullable(user.getClub())
                                      .map(Club::getId)
                                      .orElse(null)
                            : null;

                    // 그룹장이 user인 그룹 생성
                    Group group = Group.builder()
                            .masterId(user.getId())
                            .clubId(clubId)
                            .build();

                    // group에 member로 추가
                    group.addMember(user);

                    return groupRepository.save(group);
                });
    }

    public GroupResponseDto getMembersFromGroup(long userId) {

        User user = getUser(userId);
        Group group = getNullCheckedGroup(user.getGroup());

        long masterId = group.getMasterId();
        long clubId = group.getClubId();

        List<String> memberList = new ArrayList<>();
        group.getMembers().forEach(member -> {
            // memberList에 추가
            if (member.getId().equals(masterId))    // 그룹장이면 맨 앞에 추가
                memberList.add(0, member.getNickname());
            else    // 맨 뒤에 추가
                memberList.add(member.getNickname());
        });

        return GroupResponseDto.of(clubId, masterId, memberList);
    }

    @Transactional
    public long addInviteeToGroup(long userId, String inviteeNickname) {

        User invitee = userRepository.findByNickname(inviteeNickname)
                .orElseThrow(() -> new CustomException(CustomErrorCode.INVITEE_NOT_FOUND));

        // 초대 받는 유저가 가입 중인 그룹이 있으면 예외
        if (invitee.getGroup() != null && invitee.getGroup().getMembers().size() > 1) {
            throw new CustomException(CustomErrorCode.USER_BELONG_GROUP);
        }

        // 초대 받는 유저가 다른 그룹의 초대를 받는 중이면 예외
        if (notificationRepository.findSendTimeByReceiverIdAndType(invitee.getId(), NotificationType.GROUP_INVITE).stream()
                .anyMatch(sendTime -> Duration.between(sendTime, LocalDateTime.now()).getSeconds() <= 30)) {
            throw new CustomException(CustomErrorCode.USER_BEING_INVITED_GROUP);
        }

        Group group = getGroup(userId, false);  // 사용자에게 그룹이 없으면 그룹 생성
        group.addInvitee(invitee); // 초대 중인 인원에 추가

        return group.getId();
    }

    @Transactional
    public List<Long> addClubMembersToGroupInvitee(long userId) {

        User user = getUser(userId);
        if (user.getClub() == null) {   // 가입 중인 클럽이 없을 경우 예외
            throw new CustomException(CustomErrorCode.HAS_NOT_CLUB);
        }

        Group group = getGroup(userId, true);
        List<Long> inviteeIdList = new ArrayList<>();
        List<User> members = user.getClub().getMembers();
        members.forEach(member -> {
            log.info("[로그] 클럽원 {} 초대 가능 여부 확인 - status: {}, hasGroup: {}, isInviter: {}", member.getNickname(), member.getStatus(), (member.getGroup() != null), Objects.equals(member.getId(), user.getId()));
            // 유저가 매칭 가능 상태이고 그룹에 가입 중이 아니면서 그룹 초대 메시지를 받지 않은 클럽원 모두에게 그룹 초대 전송
            if (Status.WAITING == member.getStatus()
                    && member.getGroup() == null
                    && !Objects.equals(member.getId(), user.getId())    // 초대 대상에서 본인은 제외
                    && notificationRepository.findSendTimeByReceiverIdAndType(member.getId(), NotificationType.GROUP_INVITE).stream()
                    .noneMatch(sendTime -> Duration.between(sendTime, LocalDateTime.now()).getSeconds() <= 30)
            ) {
                log.info("[로그] 클럽원 {} 그룹에 추가", member.getNickname());
                group.addInvitee(member);
                inviteeIdList.add(member.getId());
                notificationService.sendNotification(userId, member.getNickname(), NotificationType.GROUP_INVITE);
            }
        });
        if (inviteeIdList.isEmpty() && userRepository.countByGroupId(group.getId()) == 1) {
            disbandGroup(group);
            throw new CustomException(CustomErrorCode.NO_AVAILABLE_CLUB_MEMBERS);
        }

        return inviteeIdList;
    }

    @Transactional
    public long updateInvitee(long userId, GroupAcceptDto groupAcceptDto) {

        User user = getUser(userId);
        Group group = getNullCheckedGroup(user.getInvitedGroup());
        Notification notification = notificationRepository.findById(groupAcceptDto.getNotificationId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOTIFICATION_NOT_FOUND));

        if (groupAcceptDto.getIsAccepted()) {
            // 해당 유저에게 온 알림이 아니면 예외
            if (notification.getReceiver().getId() != userId) {
                throw new CustomException(CustomErrorCode.NO_AUTHORITY);
            }

            // 만약 초대를 수락했는데 초대 메시지를 받은 지 30초가 지난 상태라면 초대 거절
            if (Duration.between(notification.getSendTime(), LocalDateTime.now()).getSeconds() > 30) {
                throw new CustomException(CustomErrorCode.GROUP_INVITE_TIME_OVER);
            }

            // 그룹이 존재하면 탈퇴
            if (user.getGroup() != null) {
                leaveGroup(user.getId());
            }

            // 초대 수락 메시지 전송 후 그룹에 유저 추가
            notificationService.sendNotification(userId, notification.getSender().getNickname(), NotificationType.GROUP_ACCEPT);
            group.addMember(user);
        } else {
            notificationService.sendNotification(userId, notification.getSender().getNickname(), NotificationType.GROUP_REJECT);
        }

        group.excludeInvitee(user); // 초대 목록에서 삭제
        notificationRepository.delete(notification);    // 초대 메시지 삭제

        return group.getId();
    }

    @Transactional
    public void kickMemberFromGroup(long userId, long memberId) {

        User user = getUser(userId);
        User member = getUser(memberId);
        Group group = getNullCheckedGroup(user.getGroup());

        group.kickMember(member);
    }

    @Transactional
    public void leaveGroup(long userId) {

        User user = getUser(userId);
        Group group = getNullCheckedGroup(user.getGroup());

        if (Objects.equals(group.getMasterId(), userId)) {    // 그룹장이 탈퇴하는 경우

            if (userRepository.countByGroupId(group.getId()) == 1) {
                disbandGroup(group);    // 그룹 해체
            } else {
                group.handOverMaster(group.getMembers().get(0).getId());    // 다른 그룹원에게 그룹장 양도
                user.leaveGroup();    // 그룹 탈퇴
            }
        }

    }

    public void disbandGroup(Group group) {

        group.kickAllMembers();
        group.excludeAllInvitees(); // 초대 중인 유저 목록 제거
        // TODO 알림 구현 시 알림 삭제도 추가
        groupRepository.deleteById(group.getId());  // 그룹 삭제
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

    private Group getNullCheckedGroup(Group group) {
        return Optional.ofNullable(group)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NO_JOINING_GROUP));
    }

}
