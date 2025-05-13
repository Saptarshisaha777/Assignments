package com.assignment.DocIngest.dto;

import com.assignment.DocIngest.entity.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private Role role;
}
