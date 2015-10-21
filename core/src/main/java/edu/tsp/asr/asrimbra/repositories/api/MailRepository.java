package edu.tsp.asr.asrimbra.repositories.api;

import edu.tsp.asr.asrimbra.entities.Mail;
import edu.tsp.asr.asrimbra.entities.User;
import edu.tsp.asr.asrimbra.exceptions.MailNotFoundException;
import edu.tsp.asr.asrimbra.exceptions.StorageException;

import java.util.List;

public interface MailRepository {
    List<Mail> getByUser(User user);
    List<Mail> getByUserMail(String userMail);
    Mail getByUserAndId(User user, Integer id) throws MailNotFoundException;
    Mail getByUserMailAndId(String userMail, Integer id) throws MailNotFoundException;
    void add(Mail mail) throws StorageException;
    void remove(Mail mail);
}
