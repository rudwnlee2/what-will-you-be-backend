package com.example.whatwillyoube.whatwillyoube_backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("GROUP") // DTYPE 컬럼에 저장될 값
@Getter
@Table(name = "group_mission")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMission extends Missions {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_groups_id")
    private Groups groups;
}