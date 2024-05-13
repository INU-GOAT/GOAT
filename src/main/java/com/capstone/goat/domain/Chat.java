package com.capstone.goat.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(columnDefinition = "text")
    private String comment;

    @CreationTimestamp
    private LocalDateTime time;

    private Long gameId;

    private String userNickname;

    @Builder
    public Chat(String comment, LocalDateTime time, Long gameId, String userNickname) {
        this.comment = comment;
        this.time = time;
        this.gameId = gameId;
        this.userNickname = userNickname;
    }

    public void changeMessage(String comment){
        this.comment = comment;
    }
}
