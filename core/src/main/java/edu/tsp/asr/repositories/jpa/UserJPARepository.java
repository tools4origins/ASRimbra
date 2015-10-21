package edu.tsp.asr.repositories.jpa;

import edu.tsp.asr.entities.Role;
import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.ExistingUserException;
import edu.tsp.asr.exceptions.StorageException;
import edu.tsp.asr.exceptions.UserNotFoundException;
import edu.tsp.asr.repositories.api.UserRepository;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.Optional;

public class UserJPARepository implements UserRepository {
    private SessionFactory factory;

    public UserJPARepository() {
      try {
         factory = new Configuration().configure("META-INF/hibernateUser.cfg.xml").buildSessionFactory();
      } catch (Throwable ex) {
         System.err.println("Failed to create sessionFactory object." + ex);
         throw new ExceptionInInitializerError(ex);
      }
    }

    @Override
    public void add(User user) throws ExistingUserException, StorageException {
        Session session = factory.openSession();
        try {
            session.save(user);
        } catch (HibernateException e) {
            e.printStackTrace();
            throw new StorageException();
        }
    }

    @Override
    public void remove(User user) throws StorageException {
        Session session = factory.openSession();
        try {
            session.delete(user);
        } catch (HibernateException e) {
            e.printStackTrace();
            throw new StorageException();
        }
    }

    @Override
    public void removeByMail(String mail) throws UserNotFoundException, StorageException {
        remove(getByMail(mail));
    }

    @Override
    @SuppressWarnings("unchecked") // Criteria.list() signature says it returns a List, not a List<User>
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
        User user = null;
        List<User> users;
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
            System.out.println("User not found, "+login);
            // returned value is an Optional, we allow user to stay null
        }

        return Optional.ofNullable(role);
    }
}
