package edu.tsp.asr.asrimbra.repositories.jpa;

import edu.tsp.asr.asrimbra.exceptions.StorageException;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

public class JPAHelper {
    public static void handleHibernateException(HibernateException e, Transaction tx) throws StorageException {
        if (tx != null) {
            tx.rollback();
        }
        handleHibernateException(e);
    }

    public static void handleHibernateException(HibernateException e) throws StorageException {
        e.getCause().printStackTrace();
        e.printStackTrace();
        throw new StorageException();
    }
}
