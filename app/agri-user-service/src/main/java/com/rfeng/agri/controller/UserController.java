package com.rfeng.agri.controller;
import com.rfeng.agri.model.entity.userentity.User;
import com.rfeng.agri.service.UserService;
import com.rfeng.agri.util.MinioUtil;
import com.rfeng.agri.util.UserContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 齐洪乾
 * @version 1.00
 * @time 2025/12/16 17:12
 */
@Slf4j
@RestController
@Tag(name = "用户管理", description = "用户注册、头像上传等相关接口")
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Operation(summary = "临时上传头像", description = "注册前上传头像用于预览")
    @PostMapping("/avatar/temp-upload")
    public Map<String, Object> uploadTempAvatar(@RequestParam MultipartFile avatar) {
        Map<String, Object> result = new HashMap<>();

        try {
            String avatarUrl = minioUtil.uploadTempAvatar(avatar);

            result.put("success", true);
            result.put("message", "临时头像上传成功");
            result.put("avatarUrl", avatarUrl);
        } catch (Exception e) {
            log.error("临时头像上传失败: ", e);
            result.put("success", false);
            result.put("message", "临时头像上传失败: " + e.getMessage());
        }

        return result;
    }

    @Operation(summary = "删除临时头像", description = "用户取消注册时删除临时头像")
    @DeleteMapping("/avatar/temp-delete")
    public Map<String, Object> deleteTempAvatar(@RequestParam(required = false) String avatarUrl) {
        Map<String, Object> result = new HashMap<>();

        if (avatarUrl == null || avatarUrl.isEmpty()) {
            result.put("success", true);
            result.put("message", "无需删除");
            return result;
        }

        try {
            boolean deleted = minioUtil.deleteAvatar(avatarUrl);
            
            if (deleted) {
                result.put("success", true);
                result.put("message", "临时头像已删除");
            } else {
                result.put("success", false);
                result.put("message", "删除失败或文件不存在");
            }
        } catch (Exception e) {
            log.error("删除临时头像失败: ", e);
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }

        return result;
    }

    @Operation(summary = "用户注册", description = "用户注册接口，支持头像上传")
    @PostMapping("/register")
    public Map<String, Object> register(
            @RequestParam String phone,
            @RequestParam String password,
            @RequestParam String nickname,
            @RequestParam(required = false) String avatarUrl) {

        Map<String, Object> result = new HashMap<>();

        try {
            // 检查手机号是否已存在
            User existingUser = userService.getUserByPhone(phone);
            if (existingUser != null) {
                result.put("success", false);
                result.put("message", "手机号已注册");
                return result;
            }

            // 创建新用户
            User user = new User();
            user.setPhone(phone);
            user.setPassword(passwordEncoder.encode(password));
            user.setNickname(nickname);
            user.setRole("user");
            user.setStatus(1L);
            user.setCreateTime(new java.sql.Date(System.currentTimeMillis()));
            user.setUpdateTime(new java.sql.Date(System.currentTimeMillis()));
            
            // 如果有头像URL，直接使用（前端已经上传过了）
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                user.setAvatar(avatarUrl);
            }

            // 保存用户
            boolean saved = userService.save(user);

            if (saved) {
                result.put("success", true);
                result.put("message", "注册成功");
                result.put("user", user);
            } else {
                result.put("success", false);
                result.put("message", "注册失败");
            }
        } catch (Exception e) {
            log.error("用户注册失败: ", e);
            result.put("success", false);
            result.put("message", "注册失败: " + e.getMessage());
        }

        return result;
    }

    @Operation(summary = "上传头像", description = "为已登录用户上传头像")
    @PostMapping("/avatar/upload")
    public Map<String, Object> uploadAvatar(@RequestParam MultipartFile avatar) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 从JWT Token中获取用户ID
            Long userId = UserContextUtil.getCurrentUserId();

            String avatarUrl = minioUtil.uploadAvatar(avatar, userId);

            // 更新用户头像URL
            User user = userService.getById(userId);
            if (user != null) {
                user.setAvatar(avatarUrl);
                user.setUpdateTime(new java.sql.Date(System.currentTimeMillis()));
                userService.updateById(user);
            }

            result.put("success", true);
            result.put("message", "头像上传成功");
            result.put("avatarUrl", avatarUrl);
        } catch (Exception e) {
            log.error("头像上传失败: ", e);
            result.put("success", false);
            result.put("message", "头像上传失败: " + e.getMessage());
        }

        return result;
    }
}
