package edu.tsp.asr.asrimbra.repositories.jpa;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import edu.tsp.asr.asrimbra.entities.Role;
import edu.tsp.asr.asrimbra.entities.User;
import edu.tsp.asr.asrimbra.exceptions.ExistingUserException;
import edu.tsp.asr.asrimbra.exceptions.StorageException;
import edu.tsp.asr.asrimbra.exceptions.UserNotFoundException;
import edu.tsp.asr.asrimbra.repositories.api.UserRepository;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class UserJPARepository implements UserRepository {
    private SessionFactory factory;

    public UserJPARepository(SessionFactory factory) {
         this.factory = factory;
    }

    @Override
    public void add(User user) throws ExistingUserException, StorageException {
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            session.save(user);
            tx.commit();
        } catch (HibernateException e) {
            if(e.getCause() instanceof MySQLIntegrityConstraintViolationException) {
                // @todo: could be another constraint, improve check
                throw new ExistingUserException();
            } else {
                JPAHelper.handleHibernateException(e, tx);
            }

        }
    }

    @Override
    public void removeByMail(String mail) throws StorageException {
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            Query q = session
                    .createQuery("delete from User where mail = :mail")
                    .setParameter("mail", mail);
            q.executeUpdate();
            tx.commit();
        } catch (HibernateException e) {
            JPAHelper.handleHibernateException(e, tx);
        }
    }

    @Override
    @SuppressWarnings("unchecked") // Criteria.list() signature generate warning
    public List<User> getAll() throws StorageException {
        Session session = factory.openSession();
        List<User> users;
        try {
            users = session.createCriteria(User.class).list();
        } catch (HibernateException e) {
            e.printStackTrace();
            throw new StorageException();
        }
        return users;
    }

    @Override
    public User getByMail(String mail) throws UserNotFoundException, StorageException {
        Session session = factory.openSession();
        User user;
        try {
            user = session.get(User.class, mail);
        } catch (HibernateException e) {
            e.printStackTrace();
            throw new StorageException();
        }

        if(user == null) {
            throw new UserNotFoundException();
        }

        return user;
    }

    @Override
    public Optional<Role> getRoleByCredentials(String login, String password) throws StorageException {
        Session session = factory.openSession();
        Role role = null;
        User user;
        try {
            user = session.get(User.class, login);
            if(user == null) {
                throw new UserNotFoundException();
            }
            if(user.checkPassword(password)) {
                role = user.getRole();
            }
        } catch (HibernateException e) {
            e.printStackTrace();
            throw new StorageException();
        } catch (UserNotFoundException e) {
            // returned value is an Optional, we allow user to stay null
        }

        return Optional.ofNullable(role);
    }

    //@todo: improve me
    @Override
    public void setAdmin(String mail) throws StorageException {
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            Query q = session
                    .createQuery("update User set role='ADMIN' where mail = :mail")
                    .setParameter("mail", mail);
            q.executeUpdate();
            tx.commit();
        } catch (HibernateException e) {
            JPAHelper.handleHibernateException(e, tx);
        }
    }

    //@todo: improve me
    @Override
    public void setSimpleUser(String mail) throws StorageException {
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            Query q = session
                    .createQuery("update User set role='USER' where mail = :mail")
                    .setParameter("mail", mail);
            q.executeUpdate();
            tx.commit();
        } catch (HibernateException e) {
            JPAHelper.handleHibernateException(e, tx);
        }
    }
}
