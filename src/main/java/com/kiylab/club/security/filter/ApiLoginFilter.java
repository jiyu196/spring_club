package com.kiylab.club.security.filter;

import com.kiylab.club.security.dto.ClubAuthMemberDTO;
import com.kiylab.club.security.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.io.IOException;
import java.io.PrintWriter;

@Log4j2
public class ApiLoginFilter extends AbstractAuthenticationProcessingFilter {
  private JWTUtil jwtUtil;

  // public으로 변경한 이유 -> SecurityConfig에서 사용하기 위해
  public ApiLoginFilter(String defaultFilterProcessesUrl, JWTUtil jwtUtil) {
    super(defaultFilterProcessesUrl);
    this.jwtUtil=jwtUtil;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request
          , HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

    log.info("----------------------------------");
    log.info("ApiLoginFilter- attempAuthentication");

    // POST로 넘어온 것만 처리하겠다
    if (!"POST".equalsIgnoreCase(request.getMethod())) {
      throw new AuthenticationServiceException("Only Post requests are supported..");
      }

    String email = request.getParameter("email");
    String password = request.getParameter("pw");

    UsernamePasswordAuthenticationToken authToken
            = new UsernamePasswordAuthenticationToken(email, password);

    return getAuthenticationManager().authenticate(authToken);

//    if(email == null) {
//      throw new BadCredentialsException("email cannot be null");
//    }
//    return null;
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request
          , HttpServletResponse response, FilterChain chain
          , Authentication authResult) throws IOException, ServletException {
    log.info("------------------------------------");
    log.info("ApiLoginFilter successfulAuthentication");

//    // SecurityCOntext 생성/설정
//    SecurityContext context = SecurityContextHolder.createEmptyContext();
//    context.setAuthentication(authResult);
//    SecurityContextHolder.setContext(context);
//
//    // 세션에 저장
//    request.getSession(true )
//            .setAttribute(
//                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
//                    , context);
//
//    response.sendRedirect("/");    // 주석한 이유는 컨텍스트에 저장 안하겠다는 의미에서

    String email = ((ClubAuthMemberDTO)authResult.getPrincipal()).getUsername();
    request.setAttribute("email", email);

    String token = null;
    try {
      token = jwtUtil.generateToken(email);
      response.setContentType("text/plain");  // Token은 String데이터라서 plain으로 넣는거
      response.getOutputStream().write(token.getBytes());

      log.info(token); // 서버랑 전송된 데이터랑 같은지 확인

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request
          , HttpServletResponse response, AuthenticationException failed)
          throws IOException, ServletException {
    log.info("======================================");
    log.info("ApiLoginFilter unsuccessfulAuthentication");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    JSONObject json = new JSONObject();
    String message = failed.getMessage();
    json.put("code", 401);
    json.put("message", message);

    PrintWriter out = response.getWriter();
    out.print(json);

  }
}
