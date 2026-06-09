package com.abhiram.sessionsmanager.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String userId;
    private String password;
}
