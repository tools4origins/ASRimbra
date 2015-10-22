package edu.tsp.asr.asrimbra.repositories.jpa;

import edu.tsp.asr.asrimbra.entities.MailingList;
import edu.tsp.asr.asrimbra.exceptions.StorageException;
import edu.tsp.asr.asrimbra.repositories.api.MailingListRepository;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Optional;

public class MailingListJPARepository implements MailingListRepository {
    private SessionFactory factory;

    public MailingListJPARepository(SessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public void add(MailingList mailingList) throws StorageException {
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            session.save(mailingList);
            tx.commit();
        } catch (HibernateException e) {
            JPAHelper.handleHibernateException(e, tx);
        }
    }

    @Override
    public void remove(MailingList mailingList) throws StorageException {
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            session.delete(mailingList);
            tx.commit();
        } catch (HibernateException e) {
            JPAHelper.handleHibernateException(e, tx);
        }
    }

    @Override
    public Optional<MailingList> getByAddress(String address) throws StorageException {
        Session session = factory.openSession();
        MailingList mailingList;
        try {
            mailingList = session.get(MailingList.class, address);
        } catch (HibernateException e) {
            e.printStackTrace();
            throw new StorageException();
        }

        return Optional.ofNullable(mailingList);
    }
}
