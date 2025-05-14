package Capstone.checkmate;

import Capstone.checkmate.service.JsonRememberMeServices;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MyUserDetailsService myUserDetailsService;
    private final DataSource dataSource;

    @Value("${spring.security.rememberme.key}")
    private String rememberKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DataSource dataSource) throws Exception {
        http
                // csrf 비활성화 (API)
                .csrf(csrf -> csrf.disable())

                // cors 설정
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration cfg = new CorsConfiguration();
                    cfg.setAllowedOrigins(List.of("http://localhost:3000"));
                    cfg.setAllowCredentials(true);
                    cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    cfg.setAllowedHeaders(List.of("*"));
                    return cfg;
                }))

                // 인증·인가 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/login",
                                "/api/signup",
                                "/api/logout",
                                "/api/me"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // Remember-Me (Persistent Token 방식)
                .rememberMe(rm -> rm
                        .rememberMeServices(jsonRememberMeServices())
                )

                // formLogin 사용 안함 (JSON API 기반)
                .formLogin(form -> form.disable())

                .httpBasic(HttpBasicConfigurer::disable)

                // 세션 동시 접속 제한
                .sessionManagement(sm -> sm
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                );

        return http.build();
    }

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Persistent-Token 저장소 (persistent_logins 테이블 사용)
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

    // JSON 로그인 컨트롤러 (Remember-Me 서비스 수동 호출)
    @Bean
    public JsonRememberMeServices jsonRememberMeServices() {
        JsonRememberMeServices service = new JsonRememberMeServices(
                rememberKey,
                myUserDetailsService,
                persistentTokenRepository()
        );

        service.setTokenValiditySeconds(60*60*24*7); // 7일 유지
        return service;
    }
}
