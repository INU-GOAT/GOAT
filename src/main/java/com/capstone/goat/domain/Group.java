package com.capstone.goat.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "group_table")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long masterId;

    private Long clubId;

    @OneToMany(mappedBy = "group",fetch = FetchType.LAZY)
    private List<User> members = new ArrayList<>();

    @OneToMany(mappedBy = "invitedGroup",fetch = FetchType.LAZY)
    private List<User> invitees = new ArrayList<>();

    @Builder
    public Group(Long masterId, Long clubId) {
        this.masterId = masterId;
        this.clubId = clubId;
    }

    public void handOverMaster(Long newMasterId) {
        this.masterId = newMasterId;
    }

    public void addMember(User member) {
        this.members.add(member);
        member.joinGroup(this);
    }

    public void kickMember(User member) {
        Iterator<User> iter = this.members.iterator();
        while (iter.hasNext()) {
            User user = iter.next();
            if (user.getId().equals(member.getId())) {
                member.leaveGroup();
                iter.remove();
                break;
            }
        }
    }

    public void kickAllMembers() {
        this.members.forEach(User::leaveGroup);
        this.members.clear();
    }

    public void addInvitee(User invitee) {
        this.invitees.add(invitee);
        invitee.changeInvitedGroup(this);
    }

    public void excludeInvitee(User invitee) {
        Iterator<User> iter = this.invitees.iterator();
        while (iter.hasNext()) {
            User user = iter.next();
            if (user.getId().equals(invitee.getId())) {
                invitee.denyInvitedGroup();
                iter.remove();
                break;
            }
        }
    }

    public void excludeAllInvitees() {
        this.invitees.forEach(User::denyInvitedGroup);
        this.invitees.clear();
    }


}
