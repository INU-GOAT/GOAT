package com.capstone.goat.service;

import com.capstone.goat.config.ClientKakao;
import com.capstone.goat.config.TokenProvider;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.request.TokenDto;
import com.capstone.goat.dto.request.UserSaveDto;
import com.capstone.goat.dto.request.UserUpdateDto;
import com.capstone.goat.dto.response.UserResponseDto;
import com.capstone.goat.exception.ex.CustomErrorCode;
import com.capstone.goat.exception.ex.CustomException;
import com.capstone.goat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final ClientKakao clientKakao;


    @Transactional
    public TokenDto OAuthLogin(String userCode){
        User user = clientKakao.getUserData(clientKakao.getUserKakaoToken(userCode));
        if(!userRepository.existsById(user.getId())){
            userRepository.save(user);
        }
        User loginedUser= userRepository.findById(user.getId()).get();
        log.info("회원 :{}",loginedUser.getId());
        return tokenProvider.createToken(String.valueOf(loginedUser.getId()),loginedUser.getRoles());

    }

    public TokenDto LoginByToken(String token){
        User user = clientKakao.getUserData(token);
        if(!userRepository.existsById(user.getId())){
            userRepository.save(user);
        }
        User loginedUser= userRepository.findById(user.getId()).get();
        return tokenProvider.createToken(String.valueOf(user.getId()),loginedUser.getRoles());

    }

    public UserResponseDto getUser(User user){
        if(user.getGender()==null){
            throw new CustomException(CustomErrorCode.NEED_JOIN);
        }
        return UserResponseDto.of(user);
    }

    @Transactional
    public Long join(Long id, UserSaveDto userSaveDto){
        User user = userRepository.findById(id).orElseThrow(()->new CustomException(CustomErrorCode.USER_NOT_FOUND));
        user.join(userSaveDto.getAge()
                ,userSaveDto.getGender()
                ,userSaveDto.getPrefer_sport()
                ,userSaveDto.getSoccer_tier()
                ,userSaveDto.getBadminton_tier()
                ,userSaveDto.getBasketball_tier()
                ,userSaveDto.getTableTennis_tier());
        return user.getId();
    }

    @Transactional
    public Long update(Long id, UserUpdateDto userUpdateDto){
        User user = userRepository.findById(id).orElseThrow(()->new CustomException(CustomErrorCode.USER_NOT_FOUND));
        user.update(userUpdateDto.getNickname()
                ,userUpdateDto.getAge()
                ,userUpdateDto.getGender()
                ,userUpdateDto.getPrefer_sport()
                ,userUpdateDto.getSoccer_tier()
                ,userUpdateDto.getBadminton_tier()
                ,userUpdateDto.getBasketball_tier()
                ,userUpdateDto.getTableTennis_tier());
        return user.getId();
    }

    @Transactional
    public void delete(User user){
        userRepository.delete(user);
    }





}
