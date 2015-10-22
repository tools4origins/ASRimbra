package edu.tsp.asr.asrimbra.repositories.memory;


import edu.tsp.asr.asrimbra.entities.Mail;
import edu.tsp.asr.asrimbra.entities.User;
import edu.tsp.asr.asrimbra.exceptions.MailNotFoundException;
import edu.tsp.asr.asrimbra.exceptions.StorageException;
import edu.tsp.asr.asrimbra.repositories.api.MailRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MailMemoryRepository implements MailRepository {
    private ArrayList<Mail> mails = new ArrayList<>();
    private Integer current_id = 0;

    @Override
    public List<Mail> getByUserMail(String userMail) {
        return mails.stream()
                .filter(m -> m.getTo().equals(userMail) || m.getFrom().equals(userMail))
                .collect(Collectors.toList());
    }

    @Override
    public Mail getByUserMailAndId(String userMail, Integer id) throws MailNotFoundException {
        return mails.stream()
                .filter(m -> m.getId().equals(id) && ( m.getTo().equals(userMail) || m.getFrom().equals(userMail) ))
                .findAny()
                .orElseThrow(MailNotFoundException::new);
    }

    @Override
    public void add(Mail mail) {
        mail.setId(++current_id);
        mails.add(mail);
    }

    @Override
    public void removeByUserMailAndId(String userMail, Integer id) throws StorageException {
        Optional<Mail> maybeMail = mails.stream()
                .filter(m -> m.getId().equals(id) && (m.getFrom().equals(userMail) || m.getTo().equals(userMail)))
                .findFirst();
        if(maybeMail.isPresent()) {
            mails.remove(maybeMail.get());
        }
    }
}
