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
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Long master_id;

    @OneToMany(mappedBy = "club",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<User> member;

    @OneToMany(mappedBy = "club",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<ClubApply> clubApplies;

    @Builder
    public Club(String name, Long master_id){
        this.name = name;
        this.master_id = master_id;
    }
}
