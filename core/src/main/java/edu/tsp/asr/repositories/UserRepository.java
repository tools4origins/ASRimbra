package edu.tsp.asr.repositories;

import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.StorageException;
import edu.tsp.asr.exceptions.UserNotFoundException;

import java.util.List;

public interface UserRepository {
    void addUser(User user) throws StorageException;

    void removeUser(User user) throws UserNotFoundException, StorageException;

    void removeUserByMail(String mail) throws UserNotFoundException, StorageException;

    List<User> getAllUsers() throws StorageException;

    User getUserByMail(String mail) throws UserNotFoundException, StorageException;

    User getUserByCredentials(String login, String password) throws UserNotFoundException, StorageException;
}
