package com.taskforge.taskforge_core_service.dtos.response;

public class LoginResponse {

    private String token;
    private String email;
    private String role;
    private boolean twoFactorAuthRequired;
    private String message;
    private int expiresIn;

}
