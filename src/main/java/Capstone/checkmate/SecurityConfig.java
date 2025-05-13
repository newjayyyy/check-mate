package Capstone.checkmate;

import Capstone.checkmate.service.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MyUserDetailsService myUserDetailsService;

    @Value("${spring.security.rememberme.key}")
    private String rememberKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 개발 끝나면 지울 예정 (csrf 보안 끄끼)
        http
                .csrf((csrf) -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/login", "api/logout", "/api/me","/api/signup").permitAll() //로그인 회원가입은 누구나 접근 가능
                        .anyRequest().authenticated() // 그 외는 인증 필요
                )

                .cors(cors -> cors.configurationSource(request -> {
                    var cfg = new CorsConfiguration();
                    cfg.setAllowedOrigins(List.of("http://localhost:3000"));
                    cfg.setAllowCredentials(true);
                    cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    cfg.setAllowedHeaders(List.of("*"));
                    return cfg;
                }))

                .formLogin(form -> form.disable())

                .rememberMe(rememberMe -> rememberMe
                        .key(rememberKey)
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(60*60*24*7) // 쿠키 유효 기간: 7일
                        .userDetailsService(myUserDetailsService))

                .httpBasic(HttpBasicConfigurer::disable);


        http.sessionManagement(sessionManagement -> sessionManagement.maximumSessions(1).maxSessionsPreventsLogin(true));

        return http.build();
    }

    //패스워드 인코더로 사용할 빈
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //로그인에서 사용
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
