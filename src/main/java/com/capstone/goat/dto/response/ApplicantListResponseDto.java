package com.capstone.goat.dto.response;

import com.capstone.goat.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "클럽지원자 리스트")
public class ApplicantListResponseDto {

    @Schema(description = "데이터베이스 id 값")
    private Long id;

    @Schema(description = "지원자 닉네임")
    private String nickname;

    @Schema(description = "나이")
    private Integer age;

    @Schema(description = "성별")
    private String gender;

    @Schema(description = "클럽의 스포츠 실력")
    private Integer tier;

    @Builder
    private ApplicantListResponseDto(Long id, String nickname, int age, String gender, int tier){
        this.id = id;
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
        this.tier = tier;
    }

    public static ApplicantListResponseDto of(User user){
        int tier;
        if(user.getPrefer_sport().equals("축구")){
            tier = user.getSoccer_tier();
        }
        else if(user.getPrefer_sport().equals("배드민턴")){
            tier = user.getBadminton_tier();
        }
        else if(user.getPrefer_sport().equals("농구")){
            tier = user.getBasketball_tier();
        }
        else{
            tier = user.getTableTennis_tier();
        }
        return ApplicantListResponseDto.builder().id(user.getId()).nickname(user.getNickname()).age(user.getAge()).gender(user.getGender()).tier(tier).build();
    }
}
