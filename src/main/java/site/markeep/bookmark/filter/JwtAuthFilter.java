package site.markeep.bookmark.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import site.markeep.bookmark.auth.TokenProvider;
import site.markeep.bookmark.auth.TokenUserInfo;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 먼저 헤더에서 토큰에서 타입이 붙은 풀 토큰값을 꺼내준다
        try {

            String token = parseBearToken(request);
            log.warn("토큰 서명이 어쨓는데! 비상! 비상! 해피해킹! 비상뵤!"+ token);

            if(token != null){

                // 순수 토큰에서 필요한 유저 정보를 TokenUserInfo 객체에 담아준다.
                TokenUserInfo userInfo = tokenProvider.validateAndGetTokenUserInfo(token);
                log.warn("토큰 서명이 어쨓는데22222! 비상! 비상! 해피해킹! 비상뵤!"+ userInfo);

                // 인증 처리
                // 여기가 스프링의 시큐리티컨테이너에 전달해서 전역적으로 인증 정보 활용하게
                AbstractAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userInfo, null);

                // 인증 완료 -> 클라 요청 정보 세팅!
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 스프링 시큐리티컨테이너에 인증 정보 객체 등록
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("토큰 서명이 위조되었습니다! 비상! 비상! 해피해킹! 비상뵤!");
        }

        filterChain.doFilter(request, response);

    }

    // 순수 토큰 값에서 토큰 타입 빼주기!
    private String parseBearToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        // 앞에 붙은 토큰 타입 Bearer 떼기
        // 순수 토큰 값만 떼오기
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")){
            return bearerToken.substring(7);
        }
        return null;
    }

}
