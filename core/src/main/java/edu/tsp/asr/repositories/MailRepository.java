package edu.tsp.asr.repositories;

import edu.tsp.asr.entities.Mail;
import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.MailNotFoundException;

import java.util.List;

public interface MailRepository {
    List<Mail> getByUser(User user);
    Mail getByUserAndId(User user, Integer id) throws MailNotFoundException;
    void add(Mail mail);
    void remove(Mail mail);
}
