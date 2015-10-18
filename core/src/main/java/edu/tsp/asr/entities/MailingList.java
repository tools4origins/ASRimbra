package edu.tsp.asr.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
@Entity
public class MailingList implements Serializable {
    private Integer id;
    private List<User> subscribers;
    private String address;

    @Id
	@Column(name="LIST_ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="LIST_ADDRESS")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @OneToMany(cascade=ALL, mappedBy="LIST")
    public List<User> getSubscribers() {
        return subscribers;
    }

    public void addSubscribers(User subscriber) {
        this.subscribers.add(subscriber);
    }

    }
