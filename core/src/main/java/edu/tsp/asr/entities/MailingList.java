package edu.tsp.asr.entities;

import java.util.List;

public class MailingList {
    private Integer id;
    private List<User> subscribers;
    private String address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<User> getSubscribers() {
        return subscribers;
    }

    public void addSubscribers(User subscriber) {
        this.subscribers.add(subscriber);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
