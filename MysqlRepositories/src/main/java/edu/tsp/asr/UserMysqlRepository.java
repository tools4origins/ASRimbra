package edu.tsp.asr;

import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.ExistingUserException;
import edu.tsp.asr.exceptions.UserNotFoundException;
import edu.tsp.asr.repositories.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

public class UserMysqlRepository implements UserRepository {
    private ArrayList<User> users = new ArrayList<>();
    private Integer current_id = 0;

    @PersistenceContext(unitName="pu1")
	private EntityManager em;


    @Override
    public void add(User user) throws ExistingUserException {
        try{
            User u=getByMail(user.getMail());
            throw new ExistingUserException();
        }catch(UserNotFoundException e){
            user.setId(++current_id);
          //  em.persist(user);
            users.add(user);
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
    public User getByCredentials(String mail, String password) throws UserNotFoundException {
        return users.stream()
                .filter(u -> u.getMail().equals(mail) && u.checkPassword(password))
                .findAny()
                .orElseThrow(UserNotFoundException::new);
    }
}
