package edu.tsp.asr.repositories.api;

import edu.tsp.asr.entities.Mail;
import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.MailNotFoundException;

import java.util.List;

public interface MailRepository {
    List<Mail> getByUser(User user);
    List<Mail> getByUserMail(String userMail);
    Mail getByUserAndId(User user, Integer id) throws MailNotFoundException;
    Mail getByUserMailAndId(String userMail, Integer id) throws MailNotFoundException;
    void add(Mail mail);
    void remove(Mail mail);
}
