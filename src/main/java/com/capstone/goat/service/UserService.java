package com.capstone.goat.service;

import com.capstone.goat.domain.Sport;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.UserResponseDto;
import com.capstone.goat.dto.UserSaveDto;
import com.capstone.goat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public Long save(UserSaveDto userSaveDto){
        String encodedPassword = passwordEncoder.encode(userSaveDto.getPassword());
        return userRepository.save(User.builder()
                .name(userSaveDto.getName())
                .phone(userSaveDto.getPhone())
                .isMan(userSaveDto.getIsMan())
                .age(userSaveDto.getAge())
                .login_id(userSaveDto.getLogin_id())
                .password(encodedPassword)
                .prefer_sport(userSaveDto.getPrefer_sport())
                .soccer_tier(userSaveDto.getSoccer_tier())
                .basketball_tier(userSaveDto.getBasketball_tier())
                .badminton_tier(userSaveDto.getBasketball_tier())
                .build()).getId();
    }

    public UserResponseDto getUser(Long id){
        User user = userRepository.findById(id).get();
        return UserResponseDto.of(user);
    }
}
