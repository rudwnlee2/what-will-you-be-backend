package com.example.whatwillyoube.whatwillyoube_backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("INDIVIDUAL") // DTYPE 컬럼에 저장될 값
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "individual_mission")
public class IndividualMission extends Missions {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
