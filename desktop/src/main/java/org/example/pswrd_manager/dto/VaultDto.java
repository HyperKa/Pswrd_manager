package org.example.pswrd_manager.dto;

public class VaultDto {
    private String encryptedVault;

    public VaultDto() {}
    public VaultDto(String encryptedVault) {
        this.encryptedVault = encryptedVault;
    }

    public String getEncryptedVault() {
        return encryptedVault;
    }

    public void setEncryptedVault(String encryptedVault) {
        this.encryptedVault = encryptedVault;
    }
}
