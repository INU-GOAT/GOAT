package com.capstone.goat.service;


import com.capstone.goat.domain.Club;
import com.capstone.goat.domain.User;
import com.capstone.goat.dto.request.ClubSaveDto;
import com.capstone.goat.exception.ex.CustomErrorCode;
import com.capstone.goat.exception.ex.CustomException;
import com.capstone.goat.repository.ClubRepository;
import com.capstone.goat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClubService {
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;

    public Long createClub(User user, ClubSaveDto clubSaveDto){
        if(user.getClub()!=null){
            throw new CustomException(CustomErrorCode.HAS_CLUB);
        }
        Club club = clubRepository.save(Club.builder().name(clubSaveDto.getName()).master_id(user.getId()).build());
        User master = userRepository.findById(user.getId()).get();
        master.joinClub(club);
        return club.getId();
    }




}
