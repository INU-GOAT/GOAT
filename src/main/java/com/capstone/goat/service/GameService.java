package com.capstone.goat.service;

import com.capstone.goat.domain.Team;
import com.capstone.goat.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

    private final TeamRepository teamRepository;

    @Transactional
    public int addGame(List<Integer> team) {
        Team team1 = Team.builder().build();

        return 0;
    }

}
