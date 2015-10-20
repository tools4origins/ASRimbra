package edu.tsp.asr;

import edu.tsp.asr.entities.Role;
import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.ExistingUserException;
import edu.tsp.asr.exceptions.UserNotFoundException;
import edu.tsp.asr.repositories.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserMemoryRepository implements UserRepository {
    private ArrayList<User> users = new ArrayList<>();
    private Integer current_id = 0;

    @Override
    public void add(User user) throws ExistingUserException {
        try{
            User u = getByMail(user.getMail());
            throw new ExistingUserException();
        } catch(UserNotFoundException e) {
            user.setId(++current_id);
            users.add(user);
        }
    }

    @Override
    public void remove(User user) {
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
        users.stream()
                .forEach(u -> System.out.println(u.getMail() + " vs " + login + " & " + u.checkPassword(password)));

        return users.stream()
                .filter(u -> u.getMail().equals(login) && u.checkPassword(password))
                .map(User::getRole)
                .findAny();
    }
}
