package edu.tsp.asr.entities;

import de.rtner.security.auth.spi.SimplePBKDF2;

import java.io.Serializable;

public class User implements Serializable {
    private Role role;
    private String mail;
    private String passwordHash;

    public User() {}

    public User(String mail, String password) {
        this.mail = mail;
        this.setPassword(password);
        this.role = Role.USER;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setAdmin() {
        this.role = Role.ADMIN;
    }

    public void setSimpleUser() {
        this.role = Role.USER;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail= mail;
    }

    public void setPassword(String password) {
        this.passwordHash = new SimplePBKDF2().deriveKeyFormatted(password);
    }

    public boolean checkPassword(String password) {
        return new SimplePBKDF2().verifyKeyFormatted(this.passwordHash, password);
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}

