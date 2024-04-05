package com.capstone.goat.repository;

import com.capstone.goat.domain.MatchMaking;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Repository // TODO @Component로 바꿔야 하나
public class MemoryMatchMakingRepository implements MatchMakingRepository {
    private static final List<MatchMaking>[][] store = new List[550][725];
    //Map<Integer, List<Matching>> store1; //1550725
    private static final int LATINIT = 3861;     // 최서단 위도 38.611111     최동단 위도 33.111944    550
    private static final int LNGINIT = 12461;    // 최북단 경도 124.610000    최남단 경도 131.869556   725

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

        System.out.println("[로그] latIndex/lngIndex : " + latIndex + " " + lngIndex);

        try {
            store[latIndex][lngIndex].add(matchMaking);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("위도와 경도 값이 대한민국 내의 값이 아닙니다.");
        }

    }

    @Override
    public List<MatchMaking> findByMatching(MatchMaking newMatchMaking) {
        int latIndex = (int) ( newMatchMaking.getLatitude() * 100 - LATINIT );  // 위도를 배열의 인덱스로 변환
        int lngIndex = (int) ( newMatchMaking.getLongitude() * 100 - LNGINIT ); // 경도를 배열의 인덱스로 변환

        List<MatchMaking> matchedList = new ArrayList<>();

        // TODO ArrayIndexOutOfBoundsException 예외 처리 필요
        for (MatchMaking matchMaking : store[latIndex][lngIndex]) {

            // 종목과 게임 시작 시간이 동일하면 리스트에 추가
            if (matchMaking.getSport().equals(newMatchMaking.getSport()) && matchMaking.getMatchStartTime().equals(newMatchMaking.getMatchStartTime()))
                matchedList.add(matchMaking);
        }

        return matchedList;
    }

    @Override
    public void deleteByGroupIdAndLatitudeAndLongitude(long groupId, float latitude, float longitude) {
        int latIndex = (int) ( latitude * 100 - LATINIT );  // 위도를 배열의 인덱스로 변환
        int lngIndex = (int) ( longitude * 100 - LNGINIT ); // 경도를 배열의 인덱스로 변환

        List<MatchMaking> removed = new ArrayList<>();

        for (MatchMaking matchMaking : store[latIndex][lngIndex]) {

            // groupId가 동일하면 삭제
            if (matchMaking.getGroupId() == groupId) {
                removed.add(matchMaking);
            }
        }
        store[latIndex][lngIndex].removeAll(removed);
    }
}
