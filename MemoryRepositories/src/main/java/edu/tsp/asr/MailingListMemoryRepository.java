package edu.tsp.asr;

import edu.tsp.asr.entities.Mail;
import edu.tsp.asr.entities.MailingList;
import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.MailNotFoundException;
import edu.tsp.asr.exceptions.MailingListNotFoundException;
import edu.tsp.asr.repositories.MailRepository;
import edu.tsp.asr.repositories.MailingListRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
