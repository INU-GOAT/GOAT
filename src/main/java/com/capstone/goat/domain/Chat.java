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

    public enum messageType{
        ENTER,
        TALK,
        QUIT
    }

    @Enumerated(EnumType.STRING)
    private messageType messageType;

    private Long gameId;

    private String userNickname;

    /*// TODO 지연 로딩 및 nickname 출력 시 쿼리 안 나가는 지 확인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_nickname", referencedColumnName = "nickname")
    private User userNickname;*/

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
