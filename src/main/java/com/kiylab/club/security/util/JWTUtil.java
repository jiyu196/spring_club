package com.kiylab.club.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import lombok.extern.log4j.Log4j2;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

@Log4j2
public class JWTUtil {

  // 글자 수 32자 이상을 권장 (HS256)
  private String sercetKeyString = "club12345678club12345678club12345678";

  private long expire = 60 * 24 * 30;  // 단위: 분(30일)

  public String generateToken(String content) throws Exception {

    SecureDigestAlgorithm alg = Jwts.SIG.HS256;  // 전체 데이터를 가지고 암호화 시키는
    byte[] keyBytes = sercetKeyString.getBytes(); // byte로 바꾸고 key값으로 넣어줘야함.
    Key key = Keys.hmacShaKeyFor(keyBytes);

    // HEADER로 전달되는 값을 Claims 클래스에 담아서 jwts에 전달
    Claims claims = Jwts.claims()
            .subject(content)
            .issuedAt(new Date())
            .expiration(Date.from(ZonedDateTime.now().plusMinutes(expire).toInstant()))
            .build();

    return Jwts.builder()
            .claims(claims)
            .signWith(key, alg)  // key값을 암호화해주는거
            .compact();
  }

  // 토큰의 유효성 검사 (토큰이 유효한지 유효하지 않는지 확인하는 부분)
  public String validateAndExtract(String tokenStr) throws Exception {
    byte[] keyBytes = sercetKeyString.getBytes();
    SecretKey key = Keys.hmacShaKeyFor(keyBytes);

    Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(tokenStr)
            .getPayload();

    return claims.getSubject();  // claims에 파싱한 다음에 getSubject에 값이 들어가는
  }
}

