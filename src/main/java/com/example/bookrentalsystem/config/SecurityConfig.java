package com.example.bookrentalsystem.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService memberDetailsService;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        // 로그인 과정에서 DB 조회를 담당하는 UserDetailsService 설정
        authenticationProvider.setUserDetailsService(memberDetailsService);

        // 비밀번호 검증용 passwordEncoder 설정
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 1. CSRF 정책 disable
        http.csrf(AbstractHttpConfigurer::disable);

        // 2. 인증/인가 설정
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/admin").hasRole("ADMIN")     // 관리자 페이지
            .requestMatchers("/member/**", "/css/**", "/js/**", "/").permitAll()
            .anyRequest().authenticated()
        );

        // 3. 로그인 설정
        http.formLogin(login -> login
            .loginPage("/member/login")
            .defaultSuccessUrl("/", true)
            .failureUrl("/member/login?error=true")
            .permitAll()
        );

        // 4. 로그아웃 설정
        http.logout(logout -> logout
            .logoutUrl("/member/logout")
            .logoutSuccessUrl("/")
            .permitAll()
        );

        // 5. AuthenticationProvider 등록
        http.authenticationProvider(authenticationProvider());

        return http.build();
    }
}
