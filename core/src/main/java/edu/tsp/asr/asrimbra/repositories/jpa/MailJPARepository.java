package edu.tsp.asr.asrimbra.repositories.jpa;

import edu.tsp.asr.asrimbra.entities.Mail;
import edu.tsp.asr.asrimbra.exceptions.MailNotFoundException;
import edu.tsp.asr.asrimbra.exceptions.StorageException;
import edu.tsp.asr.asrimbra.repositories.api.MailRepository;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class MailJPARepository implements MailRepository {
    private SessionFactory factory;

    public MailJPARepository(SessionFactory factory) {
        this.factory = factory;
    }

    @Override
    @SuppressWarnings("unchecked") // Criteria.list() signature generate warning
    public List<Mail> getByUserMail(String userMail) throws StorageException {
        Session session = factory.openSession();
        List<Mail> mails;
        try {
            mails = session.createCriteria(Mail.class)
                    .add(Restrictions.or(
                            Restrictions.eq("from", userMail),
                            Restrictions.eq("to", userMail)
                    )).list();
        } catch (HibernateException e) {
            e.printStackTrace();
            throw new StorageException();
        }
        return mails;
    }

    @Override
    @SuppressWarnings("unchecked") // Criteria.list() signature generate warning
    public Mail getByUserMailAndId(String userMail, Integer id) throws MailNotFoundException, StorageException {
        Session session = factory.openSession();
        Mail mail;
        try {
            mail = session.get(Mail.class, id);
            if(mail != null && (mail.getFrom().equals(userMail) || mail.getTo().equals(userMail))) {
                return mail;
            } else {
                throw new MailNotFoundException();
            }
        } catch (HibernateException e) {
            e.printStackTrace();
            throw new StorageException();
        }
    }

    @Override
    public void add(Mail mail) throws StorageException {
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            session.save(mail);
            tx.commit();
        } catch (HibernateException e) {
            JPAHelper.handleHibernateException(e, tx);
        }
    }

    @Override
    public void removeByUserMailAndId(String userMail, Integer id) throws StorageException {
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            Query q = session
                    .createSQLQuery("delete from Mail where id = :id AND (sender=:from OR recipient=:to)")
                    .setParameter("id", id)
                    .setParameter("from", userMail)
                    .setParameter("to", userMail);
            q.executeUpdate();
            tx.commit();
        } catch (HibernateException e) {
            JPAHelper.handleHibernateException(e, tx);
        }
    }

    @Override
    public void removeByUserMail(String userMail) throws StorageException {
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            Query q = session
                    .createSQLQuery("delete from Mail where (sender=:from OR recipient=:to)")
                    .setParameter("from", userMail)
                    .setParameter("to", userMail);
            q.executeUpdate();
            tx.commit();
        } catch (HibernateException e) {
            JPAHelper.handleHibernateException(e, tx);
        }
    }
}
