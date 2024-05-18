package com.capstone.goat.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
public class User implements UserDetails {
    @Id
    private Long id;

    @Column
    private String nickname;

    @Column
    private Integer age;

    @Column
    private String gender;

    private String prefer_sport;

    private Integer soccer_tier;
    private Integer badminton_tier;
    private Integer basketball_tier;
    private Integer tableTennis_tier;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "sport")
    private Map<Sport,Rating> ratings;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_group_id")
    private Group invitedGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name =  "club_id")
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="applying_club_id")
    private Club applyingClub;

    @OneToMany(mappedBy = "receiver",fetch = FetchType.LAZY)
    private List<Notification> receivedNotification;

    @Builder
    public User(String nickname,Long id, int age,String gender,String prefer_sport, int soccer_tier,int badminton_tier, int basketball_tier,int tableTennis_tier, List<String> roles){
        this.nickname = nickname;
        this.gender = gender;
        this.id = id;
        this.age = age;
        this.prefer_sport = prefer_sport;
        this.soccer_tier = soccer_tier;
        this.tableTennis_tier = tableTennis_tier;
        this.badminton_tier = badminton_tier;
        this.basketball_tier = basketball_tier;
        this.status = Status.WAITING;
        this.roles = roles;
    }

    public void join(String nickname, int age, String gender, String prefer_sport, int soccer_tier,int badminton_tier, int basketball_tier,int tableTennis_tier){
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
        this.prefer_sport = prefer_sport;
        this.soccer_tier = soccer_tier;
        this.badminton_tier = badminton_tier;
        this.basketball_tier = basketball_tier;
        this.tableTennis_tier = tableTennis_tier;
        this.status = Status.WAITING;
    }

    public void update(String nickname, int age, String gender, String prefer_sport){
        this.age = age;
        this.nickname = nickname;
        this.gender = gender;
        this.prefer_sport = prefer_sport;
    }

    public void changeStatus(Status status) {
        this.status = status;
    }

    public void joinClub(Club club){
        this.club = club;
    }
    public void applyClub(Club club){this.applyingClub = club;}
    public void fineApply(){
        this.applyingClub=null;
    }
    public void kickClub(){
        this.club = null;
    }

    public void joinGroup(Group group){
        this.group = group;
    }

    public void leaveGroup() { this.group = null; }

    public void changeInvitedGroup(Group group) {
        this.invitedGroup = group;
    }

    public void denyInvitedGroup() {
        this.invitedGroup = null;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.id.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
