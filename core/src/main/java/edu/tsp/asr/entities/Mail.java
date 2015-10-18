package edu.tsp.asr.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Mail implements Serializable {
    private Integer id;
    private String from;
    private String to;
    private String title;
    private String content;

    public Mail(String from, String to, String title, String content) {
        this.from = from;
        this.to = to;
        this.title = title;
        this.content = content;
    }
    @Id

	@Column(name="MAIL_ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	@Column(name="MAIL_FROM")
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Column(name="MAIL_TO")
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Column(name="MAIL_TITLE")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name="MAIL_CONTENT")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

