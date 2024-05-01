package com.capstone.goat.service;

import com.capstone.goat.domain.Rating;
import com.capstone.goat.domain.Sport;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.response.RatingResponseDto;
import com.capstone.goat.repository.GroupRepository;
import com.capstone.goat.repository.RatingRepository;
import com.capstone.goat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
    public int getRatingMean(long groupId, String sport) {

        List<User> userList = groupRepository.findUsersById(groupId);

        return (int) userList.stream()
                .mapToInt(user -> user.getRatings().get(Sport.getSport(sport)).getRatingScore()) // Integer 형식으로 매핑
                .average() // OptionalDouble 반환
                .orElseThrow(() -> new NoSuchElementException("그룹원의 rating 평균을 계산하는 중 오류가 생겼습니다. 그룹이 비어있습니다."));
    }

    public List<RatingResponseDto> getRatingList(long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("해당하는 유저가 존재하지 않습니다."));

        Map<Sport, Rating> ratingMap = user.getRatings();
        List<RatingResponseDto> ratingResponseDtoList = new ArrayList<>();
        ratingMap.forEach((sport, rating) -> ratingResponseDtoList.add(RatingResponseDto.from(rating)));

        return ratingResponseDtoList;
    }

    public RatingResponseDto getRatingBySport(long userId, String sportName) {

        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("해당하는 유저가 존재하지 않습니다."));
        
        Sport sport = Sport.getSport(sportName);

        Rating rating = user.getRatings().get(sport);
        return RatingResponseDto.from(rating);
    }
    
}
