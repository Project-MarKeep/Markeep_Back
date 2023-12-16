package site.markeep.bookmark.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;
import site.markeep.bookmark.filter.JwtAuthFilter;

@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;


    @Bean
    public BCryptPasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Security 모듈이 기본적으로 제공하는 보안 정책 해제.
        http
                .cors()
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests() // 요청 선언해서 인증/인가 검사 할건지 안할건지
                .antMatchers(HttpMethod.GET, "/folders/all").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/folders/my").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/user/**").permitAll();
                // 세션을 사용하지 않겠다! 설정
                // 사용하지 않으니 스프링 시큐리티가 세션을 사용하지 않고 존재해도 사용하지 않겠다고 설정!

        http.addFilterAfter(
                jwtAuthFilter,
                CorsFilter.class
        );

        return http.build();

    }

}