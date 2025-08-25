package com.kiylab.club.security.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Log4j2
@Getter
@Setter
@ToString
public class ClubAuthMemberDTO extends User implements OAuth2User {
  //DTO 역할 수행하는 클래스임과 동시에 스프링 시큐리티에서 인가/인증 작업에 사용
  private String email;
  private String password;
  private String name;
  private boolean fromSocial;
  private Map<String, Object> attr;

  public ClubAuthMemberDTO(
          String username,
          String password,
          boolean fromSocial,
          Collection<? extends GrantedAuthority> authorities,
          Map<String, Object> attr) {

    this(username, password, fromSocial, authorities);  //생성자
    this.attr = attr;
  }

  public ClubAuthMemberDTO(
          String username,
          String password,
          boolean fromSocial,
          Collection<? extends GrantedAuthority> authorities) {

    super(username, password, authorities);
    this.password = password;
    this.email = username;
    this.fromSocial = fromSocial;
    //password는 부모 클래스 사용하기 때문에 별도로 멤버 변수 선언하지 않음
  }

  @Override
  public Map<String, Object> getAttributes() {  // 키값이 아이디 값이라고 생각하면 됨.
    return attr == null ? Map.of() : attr;  // 소셜 로그인할 때 기타 속성들을 포함하고 있는
  }

  @Override
  public String getName() {
    // 소셜정보가 DB에 저장안되어 있을 때에는 attr에서 가져오고
    // DB에 저장되어있으면 DB에서 가져온 값으로 사용해도 됨.
    return attr != null  && attr.get("email") != null ? (String) attr.get("email") : email;
    // 또는 "sub"
  }

  @Override
  public String getPassword() {
    return password;
  }
}
