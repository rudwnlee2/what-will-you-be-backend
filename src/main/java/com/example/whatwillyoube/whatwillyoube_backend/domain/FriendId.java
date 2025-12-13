package com.example.whatwillyoube.whatwillyoube_backend.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter @Setter
public class FriendId implements Serializable {

    private Long fromMemberId;
    private Long toMemberId;

    public FriendId() {
    }

    public FriendId(Long fromMemberId, Long toMemberId) {
        this.fromMemberId = fromMemberId;
        this.toMemberId = toMemberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendId friendId = (FriendId) o;

        return Objects.equals(fromMemberId, friendId.fromMemberId) &&
                Objects.equals(toMemberId, friendId.toMemberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromMemberId, toMemberId);
    }

}
