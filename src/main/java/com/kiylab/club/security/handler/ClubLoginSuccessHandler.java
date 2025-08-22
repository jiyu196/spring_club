package com.kiylab.club.security.handler;

import com.kiylab.club.security.dto.ClubAuthMemberDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Log4j2
public class ClubLoginSuccessHandler implements AuthenticationSuccessHandler {

  private RedirectStrategy redirectStratgy = new DefaultRedirectStrategy();

  private final PasswordEncoder passwordEncoder;

  public ClubLoginSuccessHandler(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request
          , HttpServletResponse response
          , Authentication authentication) throws IOException, ServletException {
    log.info("--------------------------------------------");
    log.info("onAuthenticationSuccess");

    ClubAuthMemberDTO authMember = (ClubAuthMemberDTO)authentication.getPrincipal();

    boolean fromSocial = authMember.isFromSocial();
    log.info("Need Modify Member?: {}", fromSocial);
    log.info("authMember.email: {}", authMember.getEmail());
    log.info("authMember.password(비밀번호): {} ", authMember.getPassword());
    log.info("-----------------");
    log.info(authMember);

    boolean passwordResult = passwordEncoder.matches("1111", authMember.getPassword());

    if(fromSocial && passwordResult){
      log.info("회원정보 수정");
      redirectStratgy.sendRedirect(request, response, "/member/modify?from=social");
    }
    else {
      redirectStratgy.sendRedirect(request, response, "/sample/all");
    }
  }
}  // 이 handler는 로그인이 성공했을 때 handler임

