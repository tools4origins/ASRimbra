package edu.tsp.asr.repositories.memory;

import edu.tsp.asr.entities.Mail;
import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.MailNotFoundException;
import edu.tsp.asr.repositories.api.MailRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MailMemoryRepository implements MailRepository {
    private ArrayList<Mail> mails = new ArrayList<>();
    private Integer current_id = 0;

    @Override
    public List<Mail> getByUser(User user) {
        return mails.stream()
                .filter(m->m.getTo().equals(user.getMail()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Mail> getByUserMail(String userMail) {
        return mails.stream()
                .filter(m->m.getTo().equals(userMail))
                .collect(Collectors.toList());
    }

    @Override
    public Mail getByUserAndId(User user, Integer id) throws MailNotFoundException {
        return mails.stream()
                .filter(m -> m.getId().equals(id) && m.getTo().equals(user.getMail()))
                .findAny()
                .orElseThrow(MailNotFoundException::new);
    }

    @Override
    public Mail getByUserMailAndId(String userMail, Integer id) throws MailNotFoundException {
        return mails.stream()
                .filter(m -> m.getId().equals(id) && m.getTo().equals(userMail))
                .findAny()
                .orElseThrow(MailNotFoundException::new);
    }

    @Override
    public void add(Mail mail) {
        mail.setId(++current_id);
        mails.add(mail);
    }

    @Override
    public void remove(Mail mail) {
        mails.remove(mail);
    }
}