package com.example.lets_play.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SigninRequest {

    @NotBlank(message = "Email is required")
    @Email
    @Size(min = 1, max = 255)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6)
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
