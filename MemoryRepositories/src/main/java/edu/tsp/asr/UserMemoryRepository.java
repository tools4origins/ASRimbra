package edu.tsp.asr;

import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.UserNotFoundException;
import edu.tsp.asr.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
public class UserMemoryRepository implements UserRepository {
    private ArrayList<User> users = new ArrayList<>();
    private Integer current_id = 0;

   // @PersistenceContext(unitName="pu1")
	//private EntityManager em;


    @Override
    public void addUser(User user) {
        user.setId(++current_id);
        //em.persist(user);
        users.add(user);
    }

    @Override
    public void removeUser(User user) {
        //em.merge(user);
        //em.remove(user);
        users.remove(user);
    }

    @Override
    public void removeUserByMail(String mail) throws UserNotFoundException {
        removeUser(getUserByMail(mail));
    }

    @Override
    public List<User> getAllUsers() {
        return users;
    }

    @Override
    public User getUserByMail(String mail) throws UserNotFoundException {
        return users.stream()
                .filter(u -> u.getMail().equals(mail))
                .findAny()
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User getUserByCredentials(String mail, String password) throws UserNotFoundException {
        return users.stream()
                .filter(u -> u.getMail().equals(mail) && u.checkPassword(password))
                .findAny()
                .orElseThrow(UserNotFoundException::new);
    }
}
