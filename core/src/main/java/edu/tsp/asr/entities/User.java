package edu.tsp.asr.entities;

import de.rtner.security.auth.spi.SimplePBKDF2;

public class User {
    private Role role;
    private String mail;
    private String passwordHash;

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

    public String getMail() {
        return mail;
    }

    public void setPassword(String password) {
        this.passwordHash = new SimplePBKDF2().deriveKeyFormatted(password);
    }

    public boolean checkPassword(String password) {
        return new SimplePBKDF2().verifyKeyFormatted(this.passwordHash, password);
    }

}

