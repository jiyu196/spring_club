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
  public Map<String, Object> getAttributes() {
    return this.attr;
  }

  @Override
  public String getName() {
    return (String) attr.get("email");
  }

  @Override
  public String getPassword() {
    return password;
  }
}
