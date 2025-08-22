package com.kiylab.club.security.service;

import com.kiylab.club.entity.ClubMember;
import com.kiylab.club.entity.ClubMemberRole;
import com.kiylab.club.repository.ClubMemberRepository;
import com.kiylab.club.security.dto.ClubAuthMemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class ClubOAuthUserDetailsService extends DefaultOAuth2UserService {

  private final ClubMemberRepository repository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    log.info("=== userRequest === : " + userRequest);

    String clientName = userRequest.getClientRegistration().getClientName();
    log.info("=== clientName : " + clientName);
    log.info(userRequest.getAdditionalParameters());

    OAuth2User oAuth2User = super.loadUser(userRequest);

    log.info("------------------------------");
    oAuth2User.getAttributes().forEach((k, v) -> {
      log.info(k + ": " + v);
    });

    String email = null;
    // 구글 로그인하는 경우
    if (clientName.equals("Google")) {
      email = oAuth2User.getAttribute("email");
    }
    log.info("email : " + email);

    ClubMember member = saveSocialMember(email);

    // DB에서 가져온 권한을 GrantedAuthority 리스트로 변환
//    List<GrantedAuthority> authorities = member.getRoleSet().stream()
//            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
//            .collect(Collectors.toList());
//    // OAuth2User를 DefaultOAuth2User로 감싸서 반환
//    // (권한과 속성, keyAttributeName 포함)
//    return new DefaultOAuth2User(
//            authorities,
//            oAuth2User.getAttributes(),
//            "email" //구글 OAuth의 key attribute
//    );

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

  // 소셜 로그인 이메일 저장하는 메서드
  private ClubMember saveSocialMember(String email) {
    // 기존에 동일한 이메일로 가입한 회원이 있는 경우 조회만
    Optional<ClubMember> result = repository.findByEmail(email, true);
    if(result.isPresent()) {
      return result.get();
    }
    // 없다면 회원 추가. 패스워드는 1111. 이름은 그냥 이메일주소
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
}
