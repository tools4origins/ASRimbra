package edu.tsp.asr.asrimbra.entities;

import java.io.Serializable;

public class Mail implements Serializable {
    private Integer id;
    private String from;
    private String to;
    private String title;
    private String content;

    public Mail() { }

    public Mail(String from, String to, String title, String content) {
        this.from = from;
        this.to = to;
        this.title = title;
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mail mail = (Mail) o;

        if (from != null ? !from.equals(mail.from) : mail.from != null) return false;
        if (to != null ? !to.equals(mail.to) : mail.to != null) return false;
        if (title != null ? !title.equals(mail.title) : mail.title != null) return false;
        return !(content != null ? !content.equals(mail.content) : mail.content != null);

    }

    @Override
    public int hashCode() {
        int result = from != null ? from.hashCode() : 0;
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }
}

