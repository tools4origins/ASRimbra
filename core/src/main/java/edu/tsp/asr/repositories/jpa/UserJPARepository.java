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
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserJPARepository implements UserRepository {
    private ArrayList<User> users = new ArrayList<>();

    @PersistenceContext(unitName="userPersistenceUnit")
	private EntityManager em;
    private SessionFactory factory;

    public UserJPARepository() {
      try {
         factory = new Configuration().configure("META-INF/hibernate.cfg.xml").buildSessionFactory();
      } catch (Throwable ex) {
         System.err.println("Failed to create sessionFactory object." + ex);
         throw new ExceptionInInitializerError(ex);
      }
    }

    @Override
    public void add(User user) throws ExistingUserException, StorageException {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(user);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            throw new StorageException();
        }

    }

    @Override
    public void remove(User user) {
        em.merge(user);
        em.remove(user);
        users.remove(user);
    }

    @Override
    public void removeByMail(String mail) throws UserNotFoundException {
        remove(getByMail(mail));
    }

    @Override
    public List<User> getAll() {
        return users;
    }

    @Override
    public User getByMail(String mail) throws UserNotFoundException {
        return users.stream()
                .filter(u -> u.getMail().equals(mail))
                .findAny()
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public Optional<Role> getRoleByCredentials(String login, String password) {
        return users.stream()
                .filter(u -> u.getMail().equals(login) && u.checkPassword(password))
                .map(User::getRole)
                .findAny();
    }
}
