package com.capstone.goat.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@Table(name = "group_table")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "group",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    List<User> users;
}
