package com.example.chat_server.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class UrlPermitUtil {
    // 免验证Url
    private List<String> urls = new ArrayList<>();

    {
        urls.add("/ws/**");
        urls.add("/api/user/login");
        urls.add("/api/user/register");
    }


    public boolean verifyUrl(String permitUrl, List<String> urlArr) {
        for (String url : urlArr) {
            for (int index = 0; index < url.length(); index++) {
                if (url.charAt(index) == '*') {
                    return true;
                }
                if (permitUrl.length() == index + 1 && url.length() == index + 1) {
                    return true;
                }
                if (index == permitUrl.length() || permitUrl.charAt(index) != url.charAt(index)) {
                    break;
                }
            }
        }
        return false;
    }

    public boolean isPermitUrl(String url) {
        // 1. 精确匹配：只放行登录和注册
        if (url.equals("/api/user/login") || url.equals("/api/user/register")) {
            return true;
        }
        // 2. 前缀匹配：放行 WebSocket 相关路径
        if (url.startsWith("/ws/")) {
            return true;
        }
        // 3. 其他路径一律拦截
        return false;
    }


    public List<String> getPermitAllUrl() {
        return urls;
    }

    public void addUrls(List<String> urls) {
        this.urls.addAll(urls);
    }
}
