package com.capstone.goat.repository;

import com.capstone.goat.domain.Matching;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Repository // TODO @Component로 바꿔야 하나
public class MemoryMatchMakingRepository implements MatchMakingRepository {
    private static final List<Matching>[][] store = new List[550][725];
    private final int LATINIT = 3861;     // 최서단 위도 38.611111     최동단 위도 33.111944    550
    private final int LNGINIT = 12461;    // 최북단 경도 124.610000    최남단 경도 131.869556   725

    public MemoryMatchMakingRepository() {
        for (List<Matching>[] storeArray : store) {
            for (List<Matching> storeList : storeArray) {
                storeList = new LinkedList<>();
            }
        }
    }

    @Override
    public void save(Matching matching) {
        int latIndex = matching.getLatitude() - LATINIT;  // 위도를 배열의 인덱스로 변환
        int lngIndex = matching.getLongitude() - LNGINIT; // 경도를 배열의 인덱스로 변환

        store[latIndex][lngIndex].add(matching);
    }

    @Override
    public List<Matching> findByMatching(Matching newMatching) {
        int latIndex = newMatching.getLatitude() - LATINIT;  // 위도를 배열의 인덱스로 변환
        int lngIndex = newMatching.getLongitude() - LNGINIT; // 경도를 배열의 인덱스로 변환

        List<Matching> matchedList = new ArrayList<>();

        // TODO ArrayIndexOutOfBoundsException 예외 처리 필요
        for (Matching matching : store[latIndex][lngIndex]) {

            // 종목과 게임 시작 시간이 동일하면 리스트에 추가
            if (matching.getSport().equals(newMatching.getSport()) && matching.getStartTime() == newMatching.getStartTime())
                matchedList.add(matching);
        }

        return matchedList;
    }

    @Override
    public void deleteByGroupIdAndLatitudeAndLongitude(Integer groupId, Integer latitude, Integer longitude) {
        int latIndex = latitude - LATINIT;  // 위도를 배열의 인덱스로 변환
        int lngIndex = longitude - LNGINIT; // 경도를 배열의 인덱스로 변환

        for (Matching matching : store[latIndex][lngIndex]) {

            // groupId가 동일하면 삭제
            if (matching.getGroupId() == groupId) {
                store[latIndex][lngIndex].remove(matching);
            }
        }
    }
}
