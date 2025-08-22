package com.kiylab.club.config;

import com.kiylab.club.security.handler.ClubLoginSuccessHandler;
import com.kiylab.club.security.service.ClubUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Log4j2
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }


//  @Bean
//  public InMemoryUserDetailsManager inDetailsService() {
//    UserDetails user = User.builder()
//            .username("user1")
//            .password(passwordEncoder().encode("1111"))
//            .roles("USER")
//            .build();
//    return new InMemoryUserDetailsManager(user);
//  }


  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // spring security 6이상버전에서 아래와 같이 변경
    http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests((auth) -> {
              auth.requestMatchers("/sample/all").permitAll()//누구나 로그인없이
                      .requestMatchers("/sample/member").hasRole("USER") //USER이상
                      .requestMatchers("/sample/admin").hasRole("ADMIN")
                      .requestMatchers("/member/modify", "/member/modify/**").hasRole("USER")
                      .requestMatchers("/error").permitAll();

            })
            .formLogin(form ->
                    form.defaultSuccessUrl("/sample/all",false))
            .logout(Customizer.withDefaults())
            .oauth2Login(form ->
                    form.successHandler(clubLoginSuccessHandler())
            );
    return http.build();
  }



  @Bean
  public ClubLoginSuccessHandler clubLoginSuccessHandler() {
    return new ClubLoginSuccessHandler(passwordEncoder());
  }
}