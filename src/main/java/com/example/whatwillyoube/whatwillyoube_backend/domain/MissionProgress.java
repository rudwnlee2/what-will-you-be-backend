package com.example.whatwillyoube.whatwillyoube_backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "mission_progress")
public class MissionProgress {

    @Id
    private Long missions_id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionState missionState;

    @Column(nullable = false)
    private LocalDateTime startedDate;

    @Column(nullable = false)
    private LocalDateTime completedDate;

    @MapsId //fk = pk 로 사용하려고
    @OneToOne
    @JoinColumn(name = "missions_id")
    private Missions missions;

}
