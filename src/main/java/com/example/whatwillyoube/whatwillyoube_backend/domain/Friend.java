package com.example.whatwillyoube.whatwillyoube_backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "friend")
@IdClass(FriendId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend {

    @Id
    @Column(name = "from_member_id", nullable = false)
    private Long fromMemberId;

    @Id
    @Column(name = "to_member_id", nullable = false)
    private Long toMemberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "invitation_status", nullable = false)
    private InvitationStatus invitationStatus;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "invited_date")
    private LocalDateTime invitedDate;

    @Column(name = "responded_date")
    private LocalDateTime respondedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_member_id", insertable = false, updatable = false)
    private Member fromMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_member_id", insertable = false, updatable = false)
    private Member toMember;

}
