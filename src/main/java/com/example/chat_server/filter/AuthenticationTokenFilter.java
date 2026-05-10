package com.example.chat_server.filter;

import com.example.chat_server.utils.JwtUtil;
import com.example.chat_server.utils.ResultUtil;
import com.example.chat_server.utils.UrlPermitUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
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
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private UrlPermitUtil urlPermitUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
            return;
        }

        String token = httpServletRequest.getHeader("token");
        String url = httpServletRequest.getRequestURI();

        // 验证url是否需要验证
        if (!urlPermitUtil.isPermitUrl(url)) {
            Claims claims = null;
            try {
                claims = JwtUtil.parseToken(token);
            } catch (Exception e) {
                httpServletResponse.setContentType("application/json;charset=UTF-8");
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                PrintWriter out = httpServletResponse.getWriter();
                out.write(ResultUtil.TokenInvalid().toJSONString(0));
                out.flush();
                out.close();
                return;
            }
            // 设置用户信息
            Map<String, Object> map = new HashMap<>();
            claims.entrySet().stream().forEach(e -> map.put(e.getKey(), e.getValue()));
            httpServletRequest.setAttribute("userinfo", map);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}