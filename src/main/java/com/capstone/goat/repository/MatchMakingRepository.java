package com.capstone.goat.repository;

import com.capstone.goat.domain.MatchMaking;

import java.util.List;

public interface MatchMakingRepository {

    void save(MatchMaking matchMaking);

    List<MatchMaking> findByMatching(MatchMaking matchMaking);

    void deleteByGroupIdAndLatitudeAndLongitude(long groupId, float latitude, float longitude);
}
