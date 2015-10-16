package edu.tsp.asr;

import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.UserNotFoundException;
import edu.tsp.asr.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class UserMemoryRepository implements UserRepository {
    private ArrayList<User> users = new ArrayList<>();
    private Integer current_id = 0;

    @Override
    public void addUser(User user) {
        user.setId(++current_id);
        users.add(user);
    }

    @Override
    public void removeUser(User user) {
        users.remove(user);
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
