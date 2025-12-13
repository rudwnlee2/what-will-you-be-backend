package com.example.whatwillyoube.whatwillyoube_backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Inheritance(strategy = InheritanceType.JOINED) // 또는 SINGLE_TABLE
@DiscriminatorColumn(name = "DTYPE") // 미션 타입을 구분할 컬럼
@Getter
@Table(name = "missions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Missions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_recommendations_id")
    private JobRecommendations jobRecommendations;
}
