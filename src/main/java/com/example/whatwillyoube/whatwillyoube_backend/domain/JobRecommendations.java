package com.example.whatwillyoube.whatwillyoube_backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "job_recommendations")
public class JobRecommendations extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String jobName;

    @Column(nullable = false)
    private String jobSum;

    @Column(nullable = false)
    private String way;

    @Column(nullable = false)
    private String major;

    @Column(nullable = false)
    private String certificate;

    @Column(nullable = false)
    private String pay;

    @Column(nullable = false)
    private String jobProspect;

    @Column(nullable = false)
    private String knowledge;

    @Column(nullable = false)
    private String jobEnvironment;

    @Column(nullable = false)
    private String jobValues;

    @Column(nullable = false)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public JobRecommendations(String jobName, String jobSum, String way, String major, 
                             String certificate, String pay, String jobProspect, 
                             String knowledge, String jobEnvironment, String jobValues, 
                             String reason, Member member) {
        this.jobName = jobName;
        this.jobSum = jobSum;
        this.way = way;
        this.major = major;
        this.certificate = certificate;
        this.pay = pay;
        this.jobProspect = jobProspect;
        this.knowledge = knowledge;
        this.jobEnvironment = jobEnvironment;
        this.jobValues = jobValues;
        this.reason = reason;
        this.member = member;
    }

    void setMember(Member member) { this.member = member; }

}
