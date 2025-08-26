package com.kiylab.club.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// CORS: Cross-Origin Resource Sharing
// 서버의 주소 또는 포트가 다른 경우 데이터를 주고 받을 수 있도록 함.

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORSFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(HttpServletRequest request
          , HttpServletResponse response
          , FilterChain filterChain) throws ServletException, IOException {

    // 허용하고자 하는 주소(url)을 설정
    // "+" : 모든 주소를 허용
    // 2번째 인자에 스트링 한개의 값만 허용
    // 이거에 접근하는 외부 포트
//    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");

    // 2개 이상 포트를 허용하고 싶을 때
//    List<String> allowOrigin = Arrays.asList(
//            "http://localhost:3000",
//            "http://localhost:4000"
//    );
//
//    String originHeader = request.getHeader("Origin");
//    if(allowOrigin.contains(originHeader)){
//      response.setHeader("Access-Control-Allow-Origin", originHeader);
//    }  // 들어온 originHeader값을 받아서 변경하는. 매번 바뀜.

    response.setHeader("Access-Control-Allow-Credentials", "true");  // 쿠키나 토큰을 사용할것인가
    response.setHeader("Access-Control-Allow-Methods", "*");
//    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
    response.setHeader("Access-Control-Max-Age", "3600"); // 초단위
    // preflight
    // OPTIONS로 먼저 데이터를 전송해 사전확인 작업
    // POST /api/data HTTP/1.1
    // Origin: http://localhost:3000
    // Content-Type: application/json
    // Authorization: Bearer ~~~~
    // 위의 데이터들을 보내려 할때

    // OPTIONS /api/data HTTP/1.1
    // Origin: http://localhost:3000
    // Content-Type: application/json
    // Authorization: Bearer ~~~~
    // 이렇게 작성.?  이런 형식을 보낼거야 라고 알려주는
    // OPTIONS로 사전에 보내는

//    response.setHeader("Access-Control-Allow-Headers", "*");
    response.setHeader("Access-Control-Allow-Headers"
            , "Origin, X-Requested-With, Content-Type, Accept, Key, Authorization");
    // Origini : 요청주소, 포트
    // X-Request-With : Ajax 요청 (비동기 요청시 만들어짐) XMLhttpRequest
    // Content-Type : Body 내용의 데이터 형식
    // Accept : 응답받을 때 데이터 형식(application/json, text/html, text/plain)
    // key : 사용자 헤더(브라우저가 생성하는 것은 아님)
    // Authorization : 인증정보

    if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      response.setStatus(HttpServletResponse.SC_OK);
    }
    else {
      filterChain.doFilter(request, response);
    }
  }
}
