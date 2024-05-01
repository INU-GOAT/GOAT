package com.capstone.goat.repository;

import com.capstone.goat.domain.MatchMaking;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Repository
public class MemoryMatchMakingRepository implements MatchMakingRepository {
    private static final List<MatchMaking>[][] store = new List[550][725];
    //Map<Integer, List<Matching>> store1; //1550725
    private static final int LATINIT = 3861;     // 최서단 위도 38.611111     최동단 위도 33.111944    550
    private static final int LNGINIT = 12461;    // 최북단 경도 124.610000    최남단 경도 131.869556   725

    private static final int RATINGVALUE = 20;   // 함께 매칭할 최대 점수 범위, 매칭 이후 시간에 따라 증가

    public MemoryMatchMakingRepository() {
        for (int i = 0; i < store.length; i++) {
            for (int j = 0; j < store[0].length; j++) {
                store[i][j] = new LinkedList<>();
            }
        }
    }

    @Override
    public void save(MatchMaking matchMaking) {
        int latIndex = (int) ( matchMaking.getLatitude() * 100 - LATINIT );  // 위도를 배열의 인덱스로 변환
        int lngIndex = (int) ( matchMaking.getLongitude() * 100 - LNGINIT ); // 경도를 배열의 인덱스로 변환

        // 위도, 경도 인덱스 범위 검사
        checkArrayIndexOutOfBoundsException(latIndex, lngIndex);

        store[latIndex][lngIndex].add(matchMaking);

    }

    @Override
    public List<MatchMaking> findByMatching(MatchMaking newMatchMaking) {
        int latIndex = (int) ( newMatchMaking.getLatitude() * 100 - LATINIT );  // 위도를 배열의 인덱스로 변환
        int lngIndex = (int) ( newMatchMaking.getLongitude() * 100 - LNGINIT ); // 경도를 배열의 인덱스로 변환

        // 위도, 경도 인덱스 범위 검사
        checkArrayIndexOutOfBoundsException(latIndex, lngIndex);

        List<MatchMaking> matchedList = new ArrayList<>();

        for (MatchMaking matchMaking : store[latIndex][lngIndex]) {

            int ratingMaxDiff = RATINGVALUE * calculateRatingWeight(newMatchMaking.getMatchingStartTime());

            // 종목과 게임 시작 시간이 동일하고 rating이 비슷하면 리스트에 추가
            if ( matchMaking.getSport().equals(newMatchMaking.getSport()) &&
                    matchMaking.getMatchStartTime().equals(newMatchMaking.getMatchStartTime()) &&
                    Math.abs(matchMaking.getRating() - newMatchMaking.getRating()) < ratingMaxDiff)
                matchedList.add(matchMaking);
        }

        return matchedList;
    }

    @Override
    public void deleteByGroupIdAndLatitudeAndLongitude(long groupId, float latitude, float longitude) {
        int latIndex = (int) ( latitude * 100 - LATINIT );  // 위도를 배열의 인덱스로 변환
        int lngIndex = (int) ( longitude * 100 - LNGINIT ); // 경도를 배열의 인덱스로 변환

        // 위도, 경도 인덱스 범위 검사
        checkArrayIndexOutOfBoundsException(latIndex, lngIndex);

        List<MatchMaking> removed = new ArrayList<>();

        for (MatchMaking matchMaking : store[latIndex][lngIndex]) {

            // groupId가 동일하면 삭제
            if (matchMaking.getGroupId() == groupId) {
                removed.add(matchMaking);
            }
        }
        store[latIndex][lngIndex].removeAll(removed);
    }

    private void checkArrayIndexOutOfBoundsException(int latIndex, int lngIndex) {
        if (latIndex >= 550 || latIndex < 0 || lngIndex >= 725 || lngIndex < 0)
            throw new ArrayIndexOutOfBoundsException("위도와 경도 값이 대한민국 내의 값이 아닙니다.");

    }

    private int calculateRatingWeight(LocalDateTime matchingStartTime) {
        Duration diff = Duration.between(matchingStartTime.toLocalTime(), LocalTime.now());
        int ratingWeight = (int) diff.toMinutes() / 20 + 1; // 20분당 1의 가중치
        if (ratingWeight > 10) ratingWeight = 10; // 최대 가중치
        return ratingWeight;
    }
}