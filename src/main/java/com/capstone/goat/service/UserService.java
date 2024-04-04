package com.capstone.goat.service;

import com.capstone.goat.config.ClientKakao;
import com.capstone.goat.config.TokenProvider;
import com.capstone.goat.domain.Group;
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

import java.time.Duration;
import java.time.LocalDateTime;

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
        LocalDateTime localDateTime = LocalDateTime.now();
        long tokenValidMillisecond = 1000L * 60 * 60 * 2 ;//2시간
        long refreshValidMillisecond = 1000L * 60 *60 *24;//24시간
        String accessToken = tokenProvider.createToken(user.getId().toString(),user.getRoles(),localDateTime);
        String refreshToken = tokenProvider.createRefreshToken(user.getId().toString(),localDateTime);
        return TokenDto.of(accessToken,refreshToken,localDateTime.plus(Duration.ofMillis(tokenValidMillisecond)).toString(),localDateTime.plus(Duration.ofMillis(refreshValidMillisecond)).toString());

    }

    public TokenDto LoginByToken(String token){
        User user = clientKakao.getUserData(token);
        if(!userRepository.existsById(user.getId())){
            userRepository.save(user);
        }
        User loginedUser= userRepository.findById(user.getId()).get();
        LocalDateTime localDateTime = LocalDateTime.now();
        long tokenValidMillisecond = 1000L * 60 * 60 * 2 ;//2시간
        long refreshValidMillisecond = 1000L * 60 *60 *24;//24시간
        String accessToken = tokenProvider.createToken(loginedUser.getId().toString(),loginedUser.getRoles(),localDateTime);
        String refreshToken = tokenProvider.createRefreshToken(loginedUser.getId().toString(),localDateTime);
        return TokenDto.of(accessToken,refreshToken,localDateTime.plus(Duration.ofMillis(tokenValidMillisecond)).toString(),localDateTime.plus(Duration.ofMillis(refreshValidMillisecond)).toString());

    }

    public TokenDto refreshToken(String token){
        Long id = Long.valueOf(tokenProvider.getUsernameByRefresh(token));
        if(!tokenProvider.validateRefreshToken(token)){
            throw new CustomException(CustomErrorCode.EXPIRED_TOKEN);
        }
        User user = userRepository.findById(id).orElseThrow(()->new CustomException(CustomErrorCode.USER_NOT_FOUND));
        LocalDateTime localDateTime = LocalDateTime.now();
        long tokenValidMillisecond = 1000L * 60 * 60 * 2 ;//2시간
        long refreshValidMillisecond = 1000L * 60 *60 *24;//24시간
        String accessToken = tokenProvider.createToken(user.getId().toString(),user.getRoles(),localDateTime);
        String refreshToken = tokenProvider.createRefreshToken(user.getId().toString(),localDateTime);
        return TokenDto.of(accessToken,refreshToken,localDateTime.plus(Duration.ofMillis(tokenValidMillisecond)).toString(),localDateTime.plus(Duration.ofMillis(refreshValidMillisecond)).toString());
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

    @Transactional
    public void joinGroup(Long userId, Group group){

        User user = userRepository.findById(userId).orElseThrow();
        user.joinGroup(group);
    }



}
