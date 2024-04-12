package com.capstone.goat.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "group_table")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id")
    private User master;

    @OneToMany(mappedBy = "group",fetch = FetchType.LAZY)
    private List<User> members;

    @OneToMany(mappedBy = "invitedGroup",fetch = FetchType.LAZY)
    private List<User> invitees;

    @Builder
    public Group(User master) {
        this.master = master;
    }
}
