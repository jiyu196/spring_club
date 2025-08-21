package com.kiylab.club.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordTest {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  public void testEncode() {
    String password = "1111";
    String enPw = passwordEncoder.encode(password);;
    System.out.println("enPw: " +  enPw);
    // matches(인코딩 전 패스웓, 인코딩 후 패스워드)
    boolean matchResult = passwordEncoder.matches(password, enPw);  // 확인하는 작업
    System.out.println("mathResult: " + matchResult);
  }
}
