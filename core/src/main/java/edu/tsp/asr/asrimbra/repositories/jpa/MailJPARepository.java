package edu.tsp.asr.asrimbra.repositories.jpa;

import edu.tsp.asr.asrimbra.entities.Mail;
import edu.tsp.asr.asrimbra.entities.User;
import edu.tsp.asr.asrimbra.exceptions.MailNotFoundException;
import edu.tsp.asr.asrimbra.exceptions.StorageException;
import edu.tsp.asr.asrimbra.repositories.api.MailRepository;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MailJPARepository implements MailRepository {
    private ArrayList<Mail> mails = new ArrayList<>();
    private Integer current_id = 0;
    private SessionFactory factory;

    public MailJPARepository() {
        try {
            factory = new Configuration().configure("META-INF/hibernateMail.cfg.xml").buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    @Override
    public List<Mail> getByUser(User user) {
        return this.getByUserMail(user.getMail());
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
                .filter(m -> m.getId().equals(id))
                .filter(m->m.getTo().equals(user.getMail()))
                .findAny()
                .orElseThrow(MailNotFoundException::new);
    }

    @Override
    public Mail getByUserMailAndId(String userMail, Integer id) throws MailNotFoundException {
        return null;
    }

    @Override
    public void add(Mail mail) throws StorageException {
        Session session = factory.openSession();
        try {
            session.save(mail);
        } catch (HibernateException e) {
            e.printStackTrace();
            throw new StorageException();
        };
    }

    @Override
    public void remove(Mail mail) {
        mails.remove(mail);
    }
}
