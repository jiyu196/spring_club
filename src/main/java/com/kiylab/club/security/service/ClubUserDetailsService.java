package com.kiylab.club.security.service;

import com.kiylab.club.entity.ClubMember;
import com.kiylab.club.entity.ClubMemberRole;
import com.kiylab.club.repository.ClubMemberRepository;
import com.kiylab.club.security.dto.ClubAuthMemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class ClubUserDetailsService implements UserDetailsService {

  private final ClubMemberRepository clubMemberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<ClubMember> result = clubMemberRepository.findByEmail(username, false);

    if (!result.isPresent()) {
      throw new UsernameNotFoundException("Check Email or Social");
    }

    ClubMember clubMember = result.get();
    log.info("clubMember={}", clubMember);

    ClubAuthMemberDTO clubAuthMember = new ClubAuthMemberDTO(
            clubMember.getEmail(),
            clubMember.getPassword(),
            clubMember.isFromSocial(),
            clubMember.getRoleSet().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                    .collect(Collectors.toSet())
    );

    clubAuthMember.setName(clubMember.getName());
    clubAuthMember.setFromSocial(clubMember.isFromSocial());

    return clubAuthMember;
  }
}
