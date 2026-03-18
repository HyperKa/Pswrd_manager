package org.example.pswrd_manager.dto;

public class LoginRequest {
    private String username;
    private String masterPassword;

    public LoginRequest() {}

    public LoginRequest(String username, String masterPassword) {
        this.username = username;
        this.masterPassword = masterPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getMasterPassword() {
        return masterPassword;
    }

    public void setUsername(String username) { this.username = username; }
    public void setMasterPassword(String masterPassword) { this.masterPassword = masterPassword; }
}
