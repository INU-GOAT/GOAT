package com.capstone.goat.repository;

import com.capstone.goat.domain.MatchMaking;
import com.capstone.goat.exception.ex.CustomErrorCode;
import com.capstone.goat.exception.ex.CustomException;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Repository
public class MemoryMatchMakingRepository implements MatchMakingRepository {
    private static final List<MatchMaking>[][] store = new List[550][725];
    //Map<Integer, List<Matching>> store1; //1550725
    private static final int LATINIT = 3311;     // 최서단 위도 38.611111     최동단 위도 33.111944    550
    private static final int LNGINIT = 12461;    // 최북단 경도 124.610000    최남단 경도 131.869556   725

    private static final int RATINGVALUE = 100;   // 함께 매칭할 최대 점수 범위, 매칭 이후 시간에 따라 증가

    public MemoryMatchMakingRepository() {
        for (int i = 0; i < store.length; i++) {
            for (int j = 0; j < store[0].length; j++) {
                store[i][j] = new LinkedList<>();
            }
        }
    }

    @Override
    public void save(MatchMaking matchMaking) {
        int latIndex = getLatIndex(matchMaking.getLatitude());
        int lngIndex = getLngIndex(matchMaking.getLongitude());

        store[latIndex][lngIndex].add(matchMaking);

    }

    @Override
    public List<MatchMaking> findByMatchingAndMatchingRange(MatchMaking newMatchMaking, int matchingRange) {
        int latIndex = getLatIndex(newMatchMaking.getLatitude());
        int lngIndex = getLngIndex(newMatchMaking.getLongitude());

        List<MatchMaking> matchedList = new ArrayList<>();

        List<MatchMaking>[][] subArray = getSubArray(latIndex, lngIndex, matchingRange);
        List<MatchMaking> subList = new ArrayList<>();
        for (List<MatchMaking>[] row : subArray) {
            for (List<MatchMaking> element : row) {
                subList.addAll(element);
            }
        }
        // 매칭 시작 시간을 기준으로 정렬
        subList.sort(Comparator.comparing(MatchMaking::getMatchingStartTime));

        for (MatchMaking matchMaking : subList) {

            int ratingMaxDiff = RATINGVALUE * calculateRatingWeight(newMatchMaking.getMatchingStartTime());

            // 종목과 게임 시작 시간이 동일하고 rating이 비슷하면 리스트에 추가
            if ( matchMaking.getSport().equals(newMatchMaking.getSport()) &&
                    matchMaking.getMatchStartTime().equals(newMatchMaking.getMatchStartTime()) &&
                    Math.abs(matchMaking.getRating() - newMatchMaking.getRating()) < ratingMaxDiff)
                matchedList.add(matchMaking);
        }

        return matchedList;
    }

    private List<MatchMaking>[][] getSubArray(int x, int y, int range) {
        int startX = Math.max(0, x - range);
        int startY = Math.max(0, y - range);
        int endX = Math.min(store.length - 1, x + range);
        int endY = Math.min(store[0].length - 1, y + range);
        int sizeX = endX - startX + 1;
        int sizeY = endY - startY + 1;

        List<MatchMaking>[][] subArray = new List[sizeX][sizeY];

        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {
                subArray[i - startX][j - startY] = store[i][j];
            }
        }

        return subArray;
    }

    @Override
    public void deleteByGroupIdAndLatitudeAndLongitude(long groupId, double latitude, double longitude) {
        int latIndex = getLatIndex(latitude);
        int lngIndex = getLngIndex(longitude);

        List<MatchMaking> removed = new ArrayList<>();

        for (MatchMaking matchMaking : store[latIndex][lngIndex]) {

            // groupId가 동일하면 삭제
            if (matchMaking.getGroupId() == groupId) {
                removed.add(matchMaking);
            }
        }
        store[latIndex][lngIndex].removeAll(removed);
    }

    // 위도를 배열의 인덱스로 변환
    private int getLatIndex(double latitude) {
        int latIndex = (int) (latitude * 100 - LATINIT);

        if (latIndex >= 550 || latIndex < 0)
            throw new CustomException(CustomErrorCode.LATITUDE_NOT_IN_KOREA);

        return latIndex;
    }

    // 경도를 배열의 인덱스로 변환
    private int getLngIndex(double longitude) {
        int lngIndex = (int) ( longitude * 100 - LNGINIT );

        if (lngIndex >= 725 || lngIndex < 0)
            throw new CustomException(CustomErrorCode.LONGITUDE_NOT_IN_KOREA);

        return lngIndex;
    }

    private int calculateRatingWeight(LocalDateTime matchingStartTime) {

        Duration diff = Duration.between(matchingStartTime.toLocalTime(), LocalTime.now());

        int ratingWeight = (int) diff.toMinutes() / 10 + 1; // 10분당 1의 가중치
        if (ratingWeight > 15) ratingWeight = 5; // 최대 가중치
        // TODO 자정이 지나면 매칭 취소 시켜야 함
        if (matchingStartTime.getDayOfMonth() != LocalDate.now().getDayOfMonth())  ratingWeight = 10;   // 매칭 후 하루가 지났으면 최대 가중치

        return ratingWeight;
    }
}