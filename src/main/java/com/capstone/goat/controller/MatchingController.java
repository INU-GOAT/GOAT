package com.capstone.goat.controller;

import com.capstone.goat.domain.User;
import com.capstone.goat.dto.request.MatchingConditionDto;
import com.capstone.goat.dto.response.ResponseDto;
import com.capstone.goat.service.GameService;
import com.capstone.goat.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/matching/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MatchingController {

    private final MatchingService matchingService;

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<?> startMatching(@AuthenticationPrincipal User user, @Valid @RequestBody MatchingConditionDto matchingConditionDto){

        matchingService.addMatching(matchingConditionDto);

        matchingService.findMatching(matchingConditionDto);

        return new ResponseEntity<>(new ResponseDto(user.getNickname(),"매칭 시작 성공"), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> stopMatching(Principal principal){

        String username = principal.getName();


        return new ResponseEntity<>(new ResponseDto(username,"매칭 중단 성공"), HttpStatus.OK);
    }
}
