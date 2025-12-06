package com.taskforge.taskforge_core_service.dtos.response;

import java.time.LocalDateTime;

public class RegisterResponse {

    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String message;
    private boolean emailSent;
    private LocalDateTime registeredAt;

}
