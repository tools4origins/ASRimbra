package edu.tsp.asr.asrimbra.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MailingList implements Serializable {
    private Integer id;
    private List<User> subscribers = new ArrayList<>();
    private String address;

    public MailingList() { }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<User> getSubscribers() {
        return subscribers;
    }

    public void removeSubscribers(User user) {
        subscribers.remove(user);
    }

    public void addSubscribers(User subscriber) {
        this.subscribers.add(subscriber);
    }

}
