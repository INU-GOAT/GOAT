package com.capstone.goat.service;

import com.capstone.goat.domain.Sport;
import com.capstone.goat.domain.Tier;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.UserSaveDto;
import com.capstone.goat.repository.PositionRepository;
import com.capstone.goat.repository.TierRepository;
import com.capstone.goat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TierRepository tierRepository;
    private final PositionRepository positionRepository;
    private final PasswordEncoder passwordEncoder;
    public Long save(UserSaveDto userSaveDto){
        String encodedPassword = passwordEncoder.encode(userSaveDto.getPassword());
        User user = userRepository.save(User.builder().name(userSaveDto.getName()).phone(userSaveDto.getPhone()).login_id(userSaveDto.getLogin_id()).password(encodedPassword).age(userSaveDto.getAge()).build());
        if(userSaveDto.getSoccer_career()!=-1){
            tierRepository.save(Tier.builder().sport(Sport.SOCCER.getName()).tier(userSaveDto.getSoccer_career()).user(user).build());
            tierRepository.save(Tier.builder().sport(Sport.FUTSAL_5.getName()).tier(userSaveDto.getSoccer_career()).user(user).build());
            tierRepository.save(Tier.builder().sport(Sport.FUTSAL_6.getName()).tier(userSaveDto.getSoccer_career()).user(user).build());
        }
        if(userSaveDto.getBasketball_career()!=1){
            tierRepository.save(Tier.builder().sport(Sport.BASKETBALL.getName()).tier(userSaveDto.getBasketball_career()).user(user).build());
        }
        if(userSaveDto.getBadminton_career()!=1){
            tierRepository.save(Tier.builder().sport(Sport.BADMINTON_1.getName()).tier(userSaveDto.getBadminton_career()).user(user).build());
            tierRepository.save(Tier.builder().sport(Sport.BADMINTON_2.getName()).tier(userSaveDto.getBadminton_career()).user(user).build());
        }

        /* 포지션 추가 예정*/
        return user.getId();
    }
}
