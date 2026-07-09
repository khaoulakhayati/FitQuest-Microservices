package com.fitquest.gamification.service;

import com.fitquest.gamification.dto.LeaderboardEntryDto;
import com.fitquest.gamification.dto.LeaderboardResponse;
import com.fitquest.gamification.repository.AchievementRepository;
import com.fitquest.gamification.repository.XPHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final XPHistoryRepository xpHistoryRepository;
    private final AchievementRepository achievementRepository;

    @Transactional(readOnly = true)
    public LeaderboardResponse globalLeaderboard(int limit) {
        int effectiveLimit = limit <= 0 ? 20 : Math.min(limit, 100);
        List<XPHistoryRepository.UserXpAggregate> aggregates = xpHistoryRepository.findLeaderboard();

        List<LeaderboardEntryDto> entries = new ArrayList<>();
        int rank = 1;
        for (XPHistoryRepository.UserXpAggregate aggregate : aggregates) {
            if (entries.size() >= effectiveLimit) {
                break;
            }
            Long userId = aggregate.getUserId();
            long totalXp = aggregate.getTotalXp() != null ? aggregate.getTotalXp() : 0L;
            entries.add(new LeaderboardEntryDto(
                    rank++,
                    userId,
                    totalXp,
                    XpService.levelFromXp(totalXp),
                    achievementRepository.countByUserId(userId)
            ));
        }
        return new LeaderboardResponse(entries, effectiveLimit);
    }
}
