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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class ClubUserDetailsService extends DefaultOAuth2UserService {

  private final ClubMemberRepository repository;
  private final PasswordEncoder passwordEncoder;
  private ClubMember saveSocialMember(String email) {
    Optional<ClubMember> result = repository.findByEmail(email, true);

    if (result.isPresent()) {
      return result.get();
    }
    ClubMember clubMember = ClubMember.builder()
            .email(email)
            .name(email)
            .password(passwordEncoder.encode("1111"))
            .fromSocial(true)
            .build();
    clubMember.addMemberRole(ClubMemberRole.USER);
    repository.save(clubMember);
    return clubMember;
  }

  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    log.info("====================================");
    log.info("userRequest={}", userRequest);


    String clientName = userRequest.getClientRegistration().getClientName();
    log.info("clientName={}", clientName);
    log.info(userRequest.getAdditionalParameters());

    OAuth2User oAuth2User = super.loadUser(userRequest);

    log.info("=======================================");
    oAuth2User.getAttributes().forEach((key, value) -> {
      log.info("key={}, value={}", key, value);
    });

    String email = null;
    if (clientName.equals("Google")) {
      email = oAuth2User.getAttributes().get("email").toString();
    }
    log.info("email={}", email);

    ClubMember member = saveSocialMember(email);
//    return oAuth2User;

    List<GrantedAuthority> authorities = member.getRoleSet().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
            .collect(Collectors.toList());

//    return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(),
//            "email");


    ClubAuthMemberDTO clubAuthMember = new ClubAuthMemberDTO(
            member.getEmail(),
            member.getPassword(),
            true,
            member.getRoleSet().stream().map(
                            role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                    .collect(Collectors.toList()),
            oAuth2User.getAttributes()
    );
    clubAuthMember.setName(member.getName());
    return clubAuthMember;

  }
}
