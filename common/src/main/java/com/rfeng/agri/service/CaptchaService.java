package com.rfeng.agri.service;

import com.rfeng.agri.util.CaptchaUtil;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaService {
    
    private final Map<String, CaptchaCache> captchaCache = new ConcurrentHashMap<>();
    @Data
    public static class CaptchaCache {
        private String code;
        private long expireTime;
        
        public CaptchaCache(String code, long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }
    
    public CaptchaUtil.CaptchaResult generateCaptcha(String phone) {
        CaptchaUtil.CaptchaResult result = CaptchaUtil.generateCaptcha();
        long expireTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
        captchaCache.put(phone, new CaptchaCache(result.getCode(), expireTime));
        return result;
    }
    
    public boolean verifyCaptcha(String phone, String code) {
        CaptchaCache cache = captchaCache.get(phone);
        if (cache == null) {
            return false;
        }
        
        if (cache.isExpired()) {
            captchaCache.remove(phone);
            return false;
        }
        
        boolean valid = code.equalsIgnoreCase(cache.getCode());
        if (valid) {
            captchaCache.remove(phone);
        }
        return valid;
    }
}
