package com.rfeng.agri.model.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private UserInfo user;
    
    @Data
    public static class UserInfo {
        private long id;
        private String phone;
        private String nickname;
        private String avatar;
        private String role;
    }
    
    public LoginResponse() {}
    
    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
