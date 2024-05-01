package com.capstone.goat.service;

import com.capstone.goat.config.ClientKakao;
import com.capstone.goat.config.TokenProvider;
import com.capstone.goat.domain.Group;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.response.TokenDto;
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
import java.util.Collections;

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
        User user1 = userRepository.findById(user.getId()).orElseThrow(()->new CustomException(CustomErrorCode.USER_NOT_FOUND));
        String club  ="없음";
        if(user.getClub()!=null){
            club = user1.getClub().getName();
        }
        return UserResponseDto.of(user1,club);
    }

    @Transactional
    public Long join(Long id, UserSaveDto userSaveDto){
        User user = userRepository.findById(id).orElseThrow(()->new CustomException(CustomErrorCode.USER_NOT_FOUND));
        int soccer=1;
        int badminton = 1;
        int basketball = 1;
        int tableTennis = 1;
        if(userRepository.existsByNickname(userSaveDto.getNickname())){
            throw new CustomException(CustomErrorCode.EXIST_NICKNAME);
        }
        if(userSaveDto.getPrefer_sport().equals("축구")){
            soccer = userSaveDto.getSoccer_tier();
        }
        else if(userSaveDto.getPrefer_sport().equals("배드민턴")){
            badminton = userSaveDto.getBadminton_tier();
        }
        else if(userSaveDto.getPrefer_sport().equals("농구")){
            basketball = user.getBasketball_tier();
        }
        else{
            tableTennis = user.getTableTennis_tier();
        }
        user.join(userSaveDto.getNickname()
                ,userSaveDto.getAge()
                ,userSaveDto.getGender()
                ,userSaveDto.getPrefer_sport()
                ,soccer
                ,badminton
                ,basketball
                ,tableTennis);
        return user.getId();
    }

    @Transactional
    public Long update(Long id, UserUpdateDto userUpdateDto){
        User user = userRepository.findById(id).orElseThrow(()->new CustomException(CustomErrorCode.USER_NOT_FOUND));
        if(!user.getNickname().equals(userUpdateDto.getNickname())&&userRepository.existsByNickname(userUpdateDto.getNickname())){
            throw new CustomException(CustomErrorCode.EXIST_NICKNAME);
        }
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
    public void outClub(Long id){
        User user = userRepository.findById(id).orElseThrow(()->new CustomException(CustomErrorCode.USER_NOT_FOUND));
        if(user.getClub()==null){
            throw new CustomException(CustomErrorCode.HAS_NOT_CLUB);
        }
        if(user.getClub().getMaster_id().equals(user.getId())){
            throw new CustomException(CustomErrorCode.MASTER_NOT_OUT);
        }
        user.kickClub();
    }

    @Transactional
    public Long createDummyUser(Long id){
        if(userRepository.existsById(id)){
            throw new CustomException(CustomErrorCode.EXIST_ID);
        }
       User user = User.builder()
               .id(id)
               .age(20)
               .roles(Collections.singletonList("ROLE_USER"))
               .basketball_tier(3)
               .badminton_tier(3)
               .soccer_tier(3)
               .tableTennis_tier(3)
               .prefer_sport("축구")
               .nickname("더미#"+id)
               .gender("남자")
               .build();
       return userRepository.save(user).getId();
    }

    public TokenDto loginDummy(Long id){
        User loginedUser = userRepository.findById(id).orElseThrow(()->new CustomException(CustomErrorCode.USER_NOT_FOUND));
        LocalDateTime localDateTime = LocalDateTime.now();
        long tokenValidMillisecond = 1000L * 60 * 60 * 2 ;//2시간
        long refreshValidMillisecond = 1000L * 60 *60 *24;//24시간
        String accessToken = tokenProvider.createToken(loginedUser.getId().toString(),loginedUser.getRoles(),localDateTime);
        String refreshToken = tokenProvider.createRefreshToken(loginedUser.getId().toString(),localDateTime);
        return TokenDto.of(accessToken,refreshToken,localDateTime.plus(Duration.ofMillis(tokenValidMillisecond)).toString(),localDateTime.plus(Duration.ofMillis(refreshValidMillisecond)).toString());

    }

    @Transactional
    public void joinGroup(Long userId, Group group){

        User user = userRepository.findById(userId).orElseThrow();
        user.joinGroup(group);
    }

    public boolean checkNickname(String nickname){
        return userRepository.existsByNickname(nickname);
    }



}
