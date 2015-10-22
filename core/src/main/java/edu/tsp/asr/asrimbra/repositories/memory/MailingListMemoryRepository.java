package edu.tsp.asr.asrimbra.repositories.memory;


import edu.tsp.asr.asrimbra.entities.MailingList;
import edu.tsp.asr.asrimbra.repositories.api.MailingListRepository;

import java.util.ArrayList;
import java.util.Optional;

public class MailingListMemoryRepository implements MailingListRepository {
    private ArrayList<MailingList> mailingLists = new ArrayList<>();

    @Override
    public void add(MailingList mailingList) {
        mailingLists.add(mailingList);
    }

    @Override
    public void remove(MailingList mailingList) {
        mailingLists.remove(mailingList);
    }

    @Override
    public Optional<MailingList> getByAddress(String address) {
        return mailingLists.stream()
                .filter(ml -> ml.getAddress().equals(address))
                .findAny();
    }
}
