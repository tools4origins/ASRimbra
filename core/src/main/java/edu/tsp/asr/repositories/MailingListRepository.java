package edu.tsp.asr.repositories;

import edu.tsp.asr.entities.MailingList;

import java.util.Optional;

public interface MailingListRepository {
    void add(MailingList mailingList);
    void remove(MailingList mailingList);
    Optional<MailingList> getByAddress(String address);
}
