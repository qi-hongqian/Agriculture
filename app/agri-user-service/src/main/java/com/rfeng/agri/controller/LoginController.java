package com.rfeng.agri.controller;

import com.rfeng.agri.model.entity.userentity.User;
import com.rfeng.agri.service.UserService;
import com.rfeng.agri.service.CaptchaService;
import com.rfeng.agri.util.CaptchaUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {
        
        Map<String, Object> result = new HashMap<>();
        
        if (!captchaService.verifyCaptcha(request.getPhone(), request.getCaptcha())) {
            result.put("success", false);
            result.put("message", "验证码错误或已过期");
            return result;
        }
        
        User user = userService.getUserByPhone(request.getPhone());
        
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            result.put("success", false);
            result.put("message", "密码错误");
            return result;
        }
        
        if (user.getStatus() != 1) {
            result.put("success", false);
            result.put("message", "用户已被禁用");
            return result;
        }
        
        result.put("success", true);
        result.put("message", "登录成功");
        result.put("user", user);
        
        return result;
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
    @PostMapping("/captcha/refresh")
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

    public static class LoginRequest {
        private String phone;
        private String password;
        private String captcha;
        
        public String getPhone() {
            return phone;
        }
        
        public void setPhone(String phone) {
            this.phone = phone;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public String getCaptcha() {
            return captcha;
        }
        
        public void setCaptcha(String captcha) {
            this.captcha = captcha;
        }
    }
}
