package com.capstone.goat.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameFinishDto {

    @NotNull(message = "result는 비어있을 수 없습니다.")
    @Pattern(regexp = "^-1|0|1$", message = "result 값은 -1, 0, 1 중 하나여야 합니다.")
    private String result;

    private String comment;

    @NotNull(message = "feedback은 비어있을 수 없습니다.")
    @Pattern(regexp = "^-1|0|1$", message = "feedback 값은 -1, 0, 1 중 하나여야 합니다.")
    private String feedback;

    public int getResult() {

        return Integer.parseInt(result);
    }

    public String getComment() {

        return comment == null
                ? ""
                : comment;
    }

    public int getFeedback() {

        return Integer.parseInt(feedback);
    }
}
