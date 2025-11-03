package com.TaskSwap.DTOs;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class UserRegistrationRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 50)
    private String fullName;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20)
    private String username;

    @Email(message = "Invalid email address")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}
