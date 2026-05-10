package com.example.chat_server.filter;

import com.example.chat_server.utils.JwtUtil;
import com.example.chat_server.utils.ResultUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    @Lazy
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
            return;
        }

        // 检查是否为免认证接口
        String requestURI = httpServletRequest.getRequestURI();
        if (requestURI.contains("/api/user/login") ||
                requestURI.contains("/api/user/register")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        //todo 部分接口应该直接放行 未配置
        String tokenName = "token";
        String token = httpServletRequest.getHeader(tokenName);
        Claims claims = null;
        try {
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            //todo 返回错误
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.getWriter().write(ResultUtil.TokenInvalid().toString());
            return;

        }
        // 设置用户信息
        Map<String, Object> map = new HashMap<>();
        claims.entrySet().stream().forEach(e -> map.put(e.getKey(), e.getValue()));
        //验证角色是否有权限
        httpServletRequest.setAttribute("userinfo", map);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
