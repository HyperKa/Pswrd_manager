package org.example.pswrd_manager.dto;

public class RegisterRequest {

    private String username;

    private String masterPassword;

    private String encryptedVault;

    public RegisterRequest(String username, String  password, String encryptedVault) {
        this.username = username;
        this.masterPassword = password;
        this.encryptedVault = encryptedVault;
    }

    public RegisterRequest() {}
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getMasterPassword() { return masterPassword; }
    public void setMasterPassword(String masterPassword) { this.masterPassword = masterPassword; }

    public String getEncryptedVault() { return encryptedVault; }
    public void setEncryptedVault(String encryptedVault) { this.encryptedVault = encryptedVault; }
}
