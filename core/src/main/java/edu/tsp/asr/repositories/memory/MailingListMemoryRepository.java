package edu.tsp.asr.repositories.memory;

import edu.tsp.asr.entities.MailingList;
import edu.tsp.asr.repositories.api.MailingListRepository;

import java.util.ArrayList;
import java.util.Optional;

public class MailingListMemoryRepository implements MailingListRepository {
    private ArrayList<MailingList> mailingLists = new ArrayList<>();
    private Integer current_id = 0;


    @Override
    public void add(MailingList mailingList) {
        mailingList.setId(++current_id);
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
