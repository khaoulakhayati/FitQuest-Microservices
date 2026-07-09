package com.fitquest.gamification.repository;

import com.fitquest.gamification.entity.XPHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface XPHistoryRepository extends JpaRepository<XPHistory, Long> {

    List<XPHistory> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT COALESCE(SUM(x.amount), 0) FROM XPHistory x WHERE x.userId = :userId")
    long sumXpByUserId(Long userId);

    @Query("""
            SELECT x.userId AS userId, SUM(x.amount) AS totalXp
            FROM XPHistory x
            GROUP BY x.userId
            ORDER BY totalXp DESC
            """)
    List<UserXpAggregate> findLeaderboard();

    interface UserXpAggregate {
        Long getUserId();

        Long getTotalXp();
    }
}
