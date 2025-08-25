package com.kiylab.club.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Log4j2
public class ApiCheckFilter extends OncePerRequestFilter {

  private AntPathMatcher antPathMatcher;
  private String pattern;

  public ApiCheckFilter(String pattern) {
    this.antPathMatcher = new AntPathMatcher();
    this.pattern = pattern;
  }

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
    if(StringUtils.hasText(authHeader)) {
      log.info("Authorization exist: {}", authHeader);
      if(authHeader.equals("123456789")) {
        checkResult = true;
      }
    }

    return checkResult;
  }

  // 시큐리티가 필터체인을 제공하는데 내가만든 패턴을 넣는.
  // 시큐리티 쪽만 별도로 체인이 되어있음
  // 필요한것만 갖다가 쓰는거
}

