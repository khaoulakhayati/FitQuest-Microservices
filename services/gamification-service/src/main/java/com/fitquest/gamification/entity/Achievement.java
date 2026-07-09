package com.fitquest.gamification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "achievements", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "badge_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    @Column(length = 500)
    private String unlockReason;

    @CreationTimestamp
    @Column(name = "unlocked_at", updatable = false)
    private Instant unlockedAt;
}
