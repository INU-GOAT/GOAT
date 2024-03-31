package com.capstone.goat.service;

import com.capstone.goat.config.ClientKakao;
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

import java.io.UnsupportedEncodingException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final ClientKakao clientKakao;


    @Transactional
    public TokenDto OAuthLogin(String userCode){
        User user = clientKakao.getUserData(clientKakao.getUserKakaoToekn(userCode));
        if(!userRepository.existsById(user.getId())){
            userRepository.save(user);
        }
        User loginedUser= userRepository.findById(user.getId()).get();
        return tokenProvider.createToken(String.valueOf(user.getId()),loginedUser.getRoles());

    }

    public UserResponseDto getUser(User user){
        return UserResponseDto.of(user);
    }

    /*@Transactional
    public Long save(UserSaveDto userSaveDto){
        if(userRepository.existsByLoginId(userSaveDto.getLogin_id())){
            throw new CustomException(CustomErrorCode.DUPLICATE_LOGIN_ID);
        }
        return userRepository.save(User.builder()
                .isMan(userSaveDto.getIsMan())
                .age(userSaveDto.getAge())
                .prefer_sport(userSaveDto.getPrefer_sport())
                .soccer_tier(userSaveDto.getSoccer_tier())
                .basketball_tier(userSaveDto.getBasketball_tier())
                .badminton_tier(userSaveDto.getBasketball_tier())
                .roles(Collections.singletonList("ROLE_USER"))
                .build()).getId();
    }

    public UserResponseDto getUser(User user){
        return UserResponseDto.of(user);
    }*/



}
