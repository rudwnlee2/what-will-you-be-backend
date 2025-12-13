package com.example.whatwillyoube.whatwillyoube_backend.repository;

import com.example.whatwillyoube.whatwillyoube_backend.domain.RecommendationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecommendationInfoRepository extends JpaRepository<RecommendationInfo, Long> {

    Optional<RecommendationInfo> findByMember_Id(Long memberId);

    //member 중심으로 바꾼 후 미사용
//    @Query("select r from RecommendationInfo r left join fetch r.member m where m.id = :memberId")
//    Optional<RecommendationInfo> findByMemberIdWithMember(@Param("memberId") Long memberId);

}
