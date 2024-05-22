package com.capstone.goat.repository;

import com.capstone.goat.domain.MatchMaking;

import java.util.List;

public interface MatchMakingRepository {

    void save(MatchMaking matchMaking);

    List<MatchMaking> findByMatchingAndMatchingRange(MatchMaking matchMaking, int matchingRange);

    void deleteByGroupIdAndLatitudeAndLongitude(long groupId, double latitude, double longitude);
}
