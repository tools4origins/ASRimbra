package edu.tsp.asr.asrimbra.repositories.api;

import edu.tsp.asr.asrimbra.entities.Mail;
import edu.tsp.asr.asrimbra.exceptions.MailNotFoundException;
import edu.tsp.asr.asrimbra.exceptions.StorageException;

import java.util.List;

public interface MailRepository {
    List<Mail> getByUserMail(String userMail) throws StorageException;
    Mail getByUserMailAndId(String userMail, Integer id) throws MailNotFoundException, StorageException;
    void add(Mail mail) throws StorageException;
    void removeByUserMailAndId(String userMail, Integer id) throws StorageException;
    void removeByUserMail(String userMail) throws StorageException;
}
