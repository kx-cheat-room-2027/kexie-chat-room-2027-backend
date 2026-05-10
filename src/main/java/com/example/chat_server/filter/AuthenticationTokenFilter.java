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

        System.out.println(">>> 拦截器触发，请求路径：" + httpServletRequest.getRequestURI());

        if ("OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
            return;
        }

        String token = httpServletRequest.getHeader("token");
        System.out.println(">>> 拿到的 Token 是：" + token);


        String url = httpServletRequest.getRequestURI();

        boolean isPermit = urlPermitUtil.isPermitUrl(url);
        System.out.println(">>> URL: " + url + " | 是否放行：" + isPermit);

        // 验证 url 是否需要验证
        if (!urlPermitUtil.isPermitUrl(url)) {
            // 1. 【新增】强制拦截：如果 Token 为空，直接返回错误
            if (token == null || token.isEmpty()) {
                httpServletResponse.setContentType("application/json;charset=UTF-8");
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                PrintWriter out = httpServletResponse.getWriter();
                out.write(ResultUtil.TokenInvalid().toString());
                out.flush();
                out.close();
                return;
            }

            // 2. 尝试解析 Token
            try {
                Claims claims = JwtUtil.parseToken(token);

                // 设置用户信息
                Map<String, Object> map = new HashMap<>();
                claims.entrySet().forEach(e -> map.put(e.getKey(), e.getValue()));
                httpServletRequest.setAttribute("userinfo", map);
            } catch (Exception e) {
                // 3. 解析失败（Token 无效或过期）
                httpServletResponse.setContentType("application/json;charset=UTF-8");
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                PrintWriter out = httpServletResponse.getWriter();
                out.write(ResultUtil.TokenInvalid().toString());
                out.flush();
                out.close();
                return;
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
