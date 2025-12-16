package com.rfeng.agri.util;

import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtTokenUtil {

    private static final String SECRET_KEY = "agriculture-platform-secret-key-2025-12-15-for-jwt-token-generation-and-validation-with-256-bits-minimum";
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public static class TokenInfo {
        private long userId;
        private String phone;
        private String nickname;
        private String avatar;
        private String role;
        private long status;
        
        public TokenInfo(long userId, String phone, String nickname, String avatar, String role, long status) {
            this.userId = userId;
            this.phone = phone;
            this.nickname = nickname;
            this.avatar = avatar;
            this.role = role;
            this.status = status;
        }

        public long getUserId() {
            return userId;
        }

        public String getPhone() {
            return phone;
        }

        public String getNickname() {
            return nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getRole() {
            return role;
        }

        public long getStatus() {
            return status;
        }
    }

    public static String generateToken(long userId, String phone, String nickname, String avatar, String role, long status) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("phone", phone);
        claims.put("nickname", nickname);
        claims.put("avatar", avatar);
        claims.put("role", role);
        claims.put("status", status);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static TokenInfo extractTokenInfo(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            long userId = ((Number) claims.get("userId")).longValue();
            String phone = (String) claims.get("phone");
            String nickname = (String) claims.get("nickname");
            String avatar = (String) claims.get("avatar");
            String role = (String) claims.get("role");
            long status = ((Number) claims.get("status")).longValue();

            return new TokenInfo(userId, phone, nickname, avatar, role, status);
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return null;
        }
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Token无效: {}", e.getMessage());
            return false;
        }
    }
}