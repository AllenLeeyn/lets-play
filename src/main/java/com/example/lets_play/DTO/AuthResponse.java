package com.example.lets_play.DTO;

public class AuthResponse {

    private String token;
    private String type = "Bearer";

    public AuthResponse(String token, String type) {
        this.token = token;
        this.type = type != null ? type : "Bearer";
    }

    public AuthResponse(String token) {
        this.token = token;
    }

    public AuthResponse() {}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
