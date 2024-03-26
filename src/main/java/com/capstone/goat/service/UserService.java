package com.capstone.goat.service;

import com.capstone.goat.config.TokenProvider;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.request.LoginDto;
import com.capstone.goat.dto.request.TokenDto;
import com.capstone.goat.dto.response.UserResponseDto;
import com.capstone.goat.dto.request.UserSaveDto;
import com.capstone.goat.exception.ex.CustomErrorCode;
import com.capstone.goat.exception.ex.CustomException;
import com.capstone.goat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    @Transactional
    public Long save(UserSaveDto userSaveDto){
        if(userRepository.existsByLoginId(userSaveDto.getLogin_id())){
            throw new CustomException(CustomErrorCode.DUPLICATE_LOGIN_ID);
        }
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
                .roles(Collections.singletonList("ROLE_USER"))
                .build()).getId();
    }

    public UserResponseDto getUser(User user){
        return UserResponseDto.of(user);
    }

    public TokenDto login(LoginDto loginDto){
        User user = userRepository.findByLoginId(loginDto.getLogin_id()).orElseThrow(()->new CustomException(CustomErrorCode.ID_NOT_FOUND));
        if(!passwordEncoder.matches(loginDto.getPassword(),user.getPassword())){
            throw new CustomException(CustomErrorCode.PASSWORD_NOT_MATCHED);
        }
        return tokenProvider.createToken(String.valueOf(user.getId()),user.getRoles());
    }


}
