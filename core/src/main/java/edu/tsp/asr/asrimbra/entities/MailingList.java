package edu.tsp.asr.asrimbra.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MailingList implements Serializable {
    private String address;

    private List<String> subscribersMails = new ArrayList<>();

    public MailingList(String address) { this.address = address; }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getSubscribersMails() {
        return subscribersMails;
    }

    public void addSubscriberMail(String subscriberMail) {
        this.subscribersMails.add(subscriberMail);
    }

    public void removeSubscriberMail(String subscriberMail) {
        this.subscribersMails.remove(subscriberMail);
    }

    public void setSubscribersMails(List<String> subscribersMails) {
        this.subscribersMails = subscribersMails;
    }
}
