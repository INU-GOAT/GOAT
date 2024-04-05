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

    private String comment;

    @CreationTimestamp
    private LocalDateTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    private String userNickname;

    /*// TODO 지연 로딩 및 nickname 출력 시 쿼리 안 나가는 지 확인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_nickname", referencedColumnName = "nickname")
    private User userNickname;*/

    @Builder
    public Chat(String comment, LocalDateTime time, Game game, String userNickname) {
        this.comment = comment;
        this.time = time;
        this.game = game;
        this.userNickname = userNickname;
    }
}
