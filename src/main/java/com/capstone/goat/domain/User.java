package com.capstone.goat.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false,unique = true)
    private String phone;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private Boolean isMan;

    private Long manner_point;
    private Integer manner_count;

    private String prefer_sport;

    private Integer soccer_tier;
    private Integer badminton_tier;
    private Integer basketball_tier;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Builder
    public User(String name, String phone, String login_id, String password,int age,boolean isMan,String prefer_sport, int soccer_tier,int badminton_tier, int basketball_tier){
        this.name = name;
        this.phone = phone;
        this.loginId = login_id;
        this.password = password;
        this.age = age;
        this.isMan = isMan;
        this.manner_point = 0L;
        this.manner_count = 0;
        this.prefer_sport = prefer_sport;
        this.soccer_tier = soccer_tier;
        this.badminton_tier = badminton_tier;
        this.basketball_tier = basketball_tier;
    }
}
