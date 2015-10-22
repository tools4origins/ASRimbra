package edu.tsp.asr.asrimbra.repositories.api;


import edu.tsp.asr.asrimbra.entities.MailingList;
import edu.tsp.asr.asrimbra.exceptions.StorageException;

import java.util.Optional;

public interface MailingListRepository {
    void add(MailingList mailingList) throws StorageException;
    void remove(MailingList mailingList) throws StorageException;
    Optional<MailingList> getByAddress(String address) throws StorageException;
}
