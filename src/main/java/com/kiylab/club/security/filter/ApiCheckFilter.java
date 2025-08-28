package com.kiylab.club.security.filter;

import com.kiylab.club.security.service.ClubUserDetailsService;
import com.kiylab.club.security.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Log4j2
public class ApiCheckFilter extends OncePerRequestFilter {

  private AntPathMatcher antPathMatcher;
  private String pattern;
  private JWTUtil jwtUtil;

  public ApiCheckFilter(String pattern, JWTUtil jwtUtil) {
    this.antPathMatcher = new AntPathMatcher();
    this.pattern = pattern;
    this.jwtUtil = jwtUtil;
  }
  // JWTUtil은 생성자가 그 클래스 안에 하나만 있는 경우니까 autoWired로 해도됨.

  @Autowired
  private ClubUserDetailsService clubUserDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request
          , HttpServletResponse response
          , FilterChain filterChain) throws ServletException, IOException {

    log.info(request.getRequestURI());
    // URL(큰 개념. 주소창 전체 내용), URI(도메인 경로 이후)

    if(antPathMatcher.match(pattern, request.getRequestURI())) {

      log.info("ApiCheckFilter=====================================");
      log.info("ApiCheckFilter=====================================");
      log.info("ApiCheckFilter=====================================");

      boolean checkHeader = checkAuthHeader(request);

      if(checkHeader) {
        // jwtToken에서 이메일을 가져온 후 처리하는 내용
        // 인증정보를 관리하는 클래스 SecurityContextHolder
        // 인증정보를 확인해서 있으면 skip
        filterChain.doFilter(request, response);
        return;
      }  // 다음 필터로 넘어가라 라는 의미로 사용하는거
      else {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=utf-8");
        JSONObject json = new JSONObject();
        String message = "FAIL CHECK API TOKEN";
        json.put("code", 403);
        json.put("message", message);

        PrintWriter out = response.getWriter();
        out.print(json);
        return;  // 여기는 시큐리티 필터 체인울 끝내라는 의미.
        // 체인을 빠져나가서 체인 다음단계를 진행하게됨

      }
    }

    filterChain.doFilter(request, response);
  }  // end of doFilterInternal

  private boolean checkAuthHeader(HttpServletRequest request) {
    boolean checkResult = false; // 초기값은 false

    String authHeader = request.getHeader("Authorization");
//    if(StringUtils.hasText(authHeader)) {
//      log.info("Authorization exist: {}", authHeader);
//      if(authHeader.equals("123456789")) {
//        checkResult = true;
//      }
//    }    // 확인하는 if 부분만 주석처리
    if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
      log.info("Authorization{}", authHeader);

      try {
        String email = jwtUtil.validateAndExtract(authHeader.substring(7));
        log.info("validate result: {}", email);

        if (email.length() > 0) {
          checkResult = true;    // 0보다 크면 true값이 전달
          // jwtToken에 저장된 id(email, username) 정보를 가지고
          // 인증 및 인가내용을 securityContextHolder에 구성하는 부분

          // 인증된 정보가 userDetails 객체에 담김
          UserDetails userDetails = clubUserDetailsService.loadUserByUsername(email);

          UsernamePasswordAuthenticationToken authToken =
                  new UsernamePasswordAuthenticationToken(
                          userDetails,
                          null,   // 비밀번호를 확인만하기 때문에 null로줌
                          userDetails.getAuthorities());

          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          // SecurityContextHolder에 저장
          SecurityContextHolder.getContext().setAuthentication(authToken);

        } // 백앤드 안에서 내부적으로 사용하는 토큰

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    // 토큰의 Authorization 속성이 있는지, 이 글자가 배열로 되어있는지 확인
    // 이 부분은 브라우저가 알아서 만들어줌. 확인만 하면 됨. (토큰 확인 설정)

    return checkResult;
  }

  // 시큐리티가 필터체인을 제공하는데 내가만든 패턴을 넣는.
  // 시큐리티 쪽만 별도로 체인이 되어있음
  // 필요한것만 갖다가 쓰는거

}

