package com.kiylab.club.security;

import com.kiylab.club.entity.ClubMember;
import com.kiylab.club.entity.ClubMemberRole;
import com.kiylab.club.repository.ClubMemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.IntStream;

@SpringBootTest
public class ClubMemberTest {

  @Autowired
  private ClubMemberRepository repository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  public void insertDummies() {
    IntStream.rangeClosed(1, 100).forEach(i -> {
      ClubMember clubMember = ClubMember.builder()
              .email("user" + i + "@kiylab.com")
              .name("사용자" + i)
              .fromSocial(false)
              .password(passwordEncoder.encode("1111"))
              .build();

      clubMember.addMemberRole(ClubMemberRole.USER);
      if (i > 80) {
        clubMember.addMemberRole(ClubMemberRole.MANAGER);
      }
      if (i > 90) {
        clubMember.addMemberRole(ClubMemberRole.ADMIN);
      }
      repository.save(clubMember);
    });
  }
}
