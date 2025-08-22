package com.kiylab.club.repository;

import com.kiylab.club.entity.ClubMember;
import com.kiylab.club.entity.ClubMemberRole;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClubMemberRepository
        extends JpaRepository<ClubMember, String> {

  @EntityGraph(attributePaths = {"roleSet"},
          type = EntityGraph.EntityGraphType.LOAD)
  @Query("select m from ClubMember m where" +
          " m.fromSocial=:social and m.email=:email")
  Optional<ClubMember> findByEmail(String email, boolean social);

  // 쿼리 사용 없이 JPA 로직으로 구성
  @EntityGraph(attributePaths = {"roleSet"},
          type = EntityGraph.EntityGraphType.LOAD)
  Optional<ClubMember> findByEmailAndFromSocial(String email, boolean social);

}
// 한번만 실행해서 감지해라 라는 뜻으로 entityGraph로 쓴거

