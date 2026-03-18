package org.example.pswrd_manager.dto;

public class VaultEntry {
    private String id;
    private String siteName;
    private String username;
    private String password;
    private String note;

    public VaultEntry() {}
    public VaultEntry(String string, String site, String user, String pass, String s) {
        this.id = string;
        this.siteName = site;
        this.username = user;
        this.password = pass;
        this.note = s;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
