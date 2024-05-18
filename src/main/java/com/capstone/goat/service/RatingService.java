package com.capstone.goat.service;

import com.capstone.goat.domain.Rating;
import com.capstone.goat.domain.Sport;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.response.RatingResponseDto;
import com.capstone.goat.exception.ex.CustomErrorCode;
import com.capstone.goat.exception.ex.CustomException;
import com.capstone.goat.repository.GroupRepository;
import com.capstone.goat.repository.RatingRepository;
import com.capstone.goat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public Rating initRating(long userId, Sport sport, int ratingNumber) {

        User user = userRepository.getReferenceById(userId);
        Rating rating = Rating.initRating(sport, ratingNumber, user);

        return ratingRepository.save(rating);
    }

    // 그룹원의 평균 rating을 계산
    public int getRatingMean(long userId, String sport) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // 그룹이 없으면 새 List에 user를 담아서 대입
        List<User> userList = Optional.ofNullable(user.getGroup())
                .map(group -> groupRepository.findUsersById(group.getId()))
                .orElseGet(() -> new ArrayList<>(List.of(user)));

        return (int) userList.stream()
                .mapToInt(groupUser -> groupUser.getRatings().get(Sport.getSport(sport)).getRatingScore()) // Integer 형식으로 매핑
                .average() // OptionalDouble 반환
                .orElseThrow(() -> new CustomException(CustomErrorCode.GROUP_NOT_FOUND));
    }

    public List<RatingResponseDto> getRatingList(long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        Map<Sport, Rating> ratingMap = user.getRatings();
        List<RatingResponseDto> ratingResponseDtoList = new ArrayList<>();
        ratingMap.forEach((sport, rating) -> ratingResponseDtoList.add(RatingResponseDto.from(rating)));

        return ratingResponseDtoList;
    }

    public RatingResponseDto getRatingBySport(long userId, String sportName) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        Sport sport = Sport.getSport(sportName);
        Rating rating = user.getRatings().get(sport);

        return RatingResponseDto.from(rating);
    }
    
}
