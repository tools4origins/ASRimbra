package edu.tsp.asr.asrimbra.repositories.api;


import edu.tsp.asr.asrimbra.entities.MailingList;

import java.util.Optional;

public interface MailingListRepository {
    void add(MailingList mailingList);
    void remove(MailingList mailingList);
    Optional<MailingList> getByAddress(String address);
}
