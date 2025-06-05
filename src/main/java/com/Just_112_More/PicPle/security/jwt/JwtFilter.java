package com.Just_112_More.PicPle.security.jwt;

import com.Just_112_More.PicPle.common.ApiResponse;
import com.Just_112_More.PicPle.exception.CustomException;
import com.Just_112_More.PicPle.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        try {

            String jwt = jwtUtil.resolveToken(httpServletRequest);
            String requestURI = httpServletRequest.getRequestURI();
            if (StringUtils.hasText(jwt) && jwtUtil.validateAccessToken(jwt)) {
                Authentication authentication = jwtUtil.getAuthenticationFromAccessToken(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
            } else {
                logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
            }
        } catch (CustomException e) {
            logger.info("JWT 만료 처리");
            ApiResponse<?> errorResponse = ApiResponse.fail(null, e.getErrorCode().name(), e.getErrorCode().getMessage());
            String json = new ObjectMapper().writeValueAsString(errorResponse);
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.setStatus(e.getErrorCode().getStatus().value());
            httpServletResponse.getWriter().write(json);
            return; // 에러처리후 필터체인 종료
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
