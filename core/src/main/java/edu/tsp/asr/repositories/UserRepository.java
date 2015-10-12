package edu.tsp.asr.repositories;

import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.UserNotFoundException;

import java.util.List;

public interface UserRepository {
    void addUser(User user);

    void removeUser(User user);

    List<User> getAllUsers();

    User getUserByMail(String mail) throws UserNotFoundException;

    User getUserByCredentials(String login, String password) throws UserNotFoundException;
}
