package com.rfeng.agri.util;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
/**
 * @author 齐洪乾
 * @version 1.00
 * @time 2025/12/16 11:53
 */
@Component
public class UserContextUtil {

    public static long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("无法获取request属性");
        }
        Object userId = attributes.getRequest().getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("未能获取userId");
        }
        return (long) userId;
    }

    public static String getCurrentPhone() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("无法获取request属性");
        }
        return (String) attributes.getRequest().getAttribute("phone");
    }

    public static String getCurrentNickname() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("无法获取request属性");
        }
        return (String) attributes.getRequest().getAttribute("nickname");
    }

    public static String getCurrentRole() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("无法获取request属性");
        }
        return (String) attributes.getRequest().getAttribute("role");
    }

    public static boolean validateUserAccess(long requestedUserId) {
        long currentUserId = getCurrentUserId();
        if (currentUserId != requestedUserId) {
            throw new RuntimeException("无权限访问其他用户的数据");
        }
        return true;
    }
}