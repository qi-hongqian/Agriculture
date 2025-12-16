package com.rfeng.agri.interceptor;

import com.rfeng.agri.util.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestPath = request.getRequestURI();
        
        if (requestPath.startsWith("/api/auth/") || 
            requestPath.startsWith("/api/user/register") ||
            requestPath.startsWith("/api/user/avatar/temp-upload") ||
            requestPath.startsWith("/api/user/avatar/temp-delete") ||
            requestPath.startsWith("/api/avatar/") ||
            requestPath.startsWith("/doc.html") || 
            requestPath.startsWith("/webjars/") ||
            requestPath.startsWith("/swagger-resources/") ||
            requestPath.startsWith("/v3/api-docs")) {
            return true;
        }
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || authHeader.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"缺少Authorization头\"}");
            return false;
        }
        
        String token = authHeader;
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (!JwtTokenUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"Token无效或已过期\"}");
            return false;
        }
        
        JwtTokenUtil.TokenInfo tokenInfo = JwtTokenUtil.extractTokenInfo(token);
        if (tokenInfo == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"Token信息提取失败\"}");
            return false;
        }
        
        request.setAttribute("userId", tokenInfo.getUserId());
        request.setAttribute("phone", tokenInfo.getPhone());
        request.setAttribute("nickname", tokenInfo.getNickname());
        request.setAttribute("avatar", tokenInfo.getAvatar());
        request.setAttribute("role", tokenInfo.getRole());
        request.setAttribute("status", tokenInfo.getStatus());
        
        log.info("用户 {} (手机号: {}) 请求路径: {}", tokenInfo.getUserId(), tokenInfo.getPhone(), requestPath);
        
        return true;
    }
}
