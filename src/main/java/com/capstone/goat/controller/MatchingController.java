package com.capstone.goat.controller;

import com.capstone.goat.domain.User;
import com.capstone.goat.dto.MatchingConditionDto;
import com.capstone.goat.dto.ResponseDto;
import com.capstone.goat.repository.UserRepository;
import com.capstone.goat.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/matching/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MatchingController {

    private final UserRepository userRepository;

    private final MatchingService matchingService;

    @PostMapping
    public ResponseEntity<?> startMatching(Principal principal, @Valid @RequestBody MatchingConditionDto matchingConditionDto){

        String loginId = principal.getName();

        User user = userRepository.findByLoginId(loginId)
                .orElse(User.builder()
                        .age(25)
                        .phone("010-1234-1234")
                        .login_id("thisisloginid")
                        .name("thisisname")
                        .isMan(true)
                        .password("thisispassword").build());

        matchingService.findMatching(user, matchingConditionDto);


        return new ResponseEntity<>(new ResponseDto(loginId,"매칭 시작 성공"), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> stopMatching(Principal principal){

        String username = principal.getName();



        return new ResponseEntity<>(new ResponseDto(username,"매칭 중단 성공"), HttpStatus.OK);
    }
}
