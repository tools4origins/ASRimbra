package edu.tsp.asr.entities;

import de.rtner.security.auth.spi.SimplePBKDF2;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class User implements Serializable {
    private Integer id;
    private Role role;
    private String mail;
    private String passwordHash;

    public User() {}
    public User(String mail, String password) {
        this.mail = mail;
        this.setPassword(password);
        this.role = Role.USER;
    }
    @Id

	@Column(name="USER_ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	@Column(name="USER_ROLE")
    public Role getRole() {
        return role;
    }

    public void setAdmin() {
        this.role = Role.ADMIN;
    }

    public void setSimpleUser() {
        this.role = Role.USER;
    }

	@Column(name="USER_Mail")
    public String getMail() {
        return mail;
    }
    public void setMail(String mail ) {
        this.mail=mail;
    }


    @Column(name="USER_PASSWD")
    public void setPassword(String password) {
        this.passwordHash = new SimplePBKDF2().deriveKeyFormatted(password);
    }

    public boolean checkPassword(String password) {
        return new SimplePBKDF2().verifyKeyFormatted(this.passwordHash, password);
    }



}

