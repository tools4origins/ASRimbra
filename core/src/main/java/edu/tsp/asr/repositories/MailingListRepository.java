package edu.tsp.asr.repositories;

import edu.tsp.asr.entities.Mail;
import edu.tsp.asr.entities.MailingList;
import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.MailNotFoundException;
import edu.tsp.asr.exceptions.MailingListNotFoundException;

import java.util.List;
import java.util.Optional;

public interface MailingListRepository {
    void add(MailingList mailingList);
    void remove(MailingList mailingList);
    Optional<MailingList> getByAddress(String address);
}
