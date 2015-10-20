package edu.tsp.asr.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

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
