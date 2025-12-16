package com.rfeng.agri.controller;

import com.rfeng.agri.model.entity.userentity.User;
import com.rfeng.agri.model.dto.LoginResponse;
import com.rfeng.agri.service.UserService;
import com.rfeng.agri.service.CaptchaService;
import com.rfeng.agri.util.CaptchaUtil;
import com.rfeng.agri.util.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "用户登录", description = "用户登录相关接口")
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private CaptchaService captchaService;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Operation(summary = "用户登录", description = "用户使用手机号和密码登录系统")
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public LoginResponse login(@RequestBody LoginRequest request) {
        
        LoginResponse response = new LoginResponse();
        
        if (!captchaService.verifyCaptcha(request.getPhone(), request.getCaptcha())) {
            response.setSuccess(false);
            response.setMessage("验证码错误或已过期");
            return response;
        }
        
        User user = userService.getUserByPhone(request.getPhone());
        
        if (user == null) {
            response.setSuccess(false);
            response.setMessage("用户不存在");
            return response;
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            response.setSuccess(false);
            response.setMessage("密码错误");
            return response;
        }
        
        if (user.getStatus() != 1) {
            response.setSuccess(false);
            response.setMessage("用户已被禁用");
            return response;
        }
        
        String token = JwtTokenUtil.generateToken(
            user.getId(), 
            user.getPhone(), 
            user.getNickname(), 
            user.getAvatar(),
            user.getRole(),
            user.getStatus()
        );

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setPhone(user.getPhone());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setRole(user.getRole());
        
        response.setSuccess(true);
        response.setMessage("登录成功");
        response.setToken(token);
        response.setUser(userInfo);
        
        return response;
    }
    
    @Operation(summary = "获取验证码", description = "获取登录验证码（返回base64格式图片）")
    @GetMapping("/captcha")
    public Map<String, Object> getCaptcha(
            @Parameter(description = "手机号") @RequestParam String phone) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            CaptchaUtil.CaptchaResult captchaResult = captchaService.generateCaptcha(phone);
            
            result.put("success", true);
            result.put("message", "验证码已生成");
            result.put("captchaImage", captchaResult.getImageBase64());
            result.put("expireTime", 300);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "验证码生成失败");
        }
        
        return result;
    }
    
    @Operation(summary = "刷新验证码", description = "刷新验证码图片")
    @PostMapping(value = "/captcha/refresh", consumes = "application/json", produces = "application/json")
    public Map<String, Object> refreshCaptcha(@RequestBody Map<String, String> request) {
        
        Map<String, Object> result = new HashMap<>();
        String phone = request.get("phone");
        
        if (phone == null || phone.isEmpty()) {
            result.put("success", false);
            result.put("message", "手机号不能为空");
            return result;
        }
        
        try {
            CaptchaUtil.CaptchaResult captchaResult = captchaService.generateCaptcha(phone);
            
            result.put("success", true);
            result.put("message", "验证码已刷新");
            result.put("captchaImage", captchaResult.getImageBase64());
            result.put("expireTime", 300);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "验证码生成失败");
        }
        
        return result;
    }

    @Data
    public static class LoginRequest {
        private String phone;
        private String password;
        private String captcha;
    }
}
