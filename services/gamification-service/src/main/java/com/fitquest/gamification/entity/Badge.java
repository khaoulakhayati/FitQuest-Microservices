package com.fitquest.gamification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "badges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(length = 512)
    private String iconUrl;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private int xpThreshold;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;
}
