package com.capstone.goat.repository;

import com.capstone.goat.domain.Matching;

import java.util.List;

public interface MatchMakingRepository {

    void save(Matching matching);

    List<Matching> findByMatching(Matching matching);

    void deleteByGroupIdAndLatitudeAndLongitude(Integer groupId, Integer latitude, Integer longitude);
}
