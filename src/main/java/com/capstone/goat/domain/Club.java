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

    @Column
    private String sport;

    @Column
    private Long win;

    @Column
    private Long lose;

    @OneToMany(mappedBy = "club",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<User> members;

    @OneToMany(mappedBy = "applyingClub",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<User> applicants;



    @Builder
    public Club(String name, Long master_id, String sport){
        this.name = name;
        this.master_id = master_id;
        this.sport = sport;
        this.win = 0L;
        this.lose = 0L;
    }
}
