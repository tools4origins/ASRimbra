package edu.tsp.asr.repositories;

import edu.tsp.asr.entities.Role;
import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.ExistingUserException;
import edu.tsp.asr.exceptions.StorageException;
import edu.tsp.asr.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void add(User user) throws StorageException, ExistingUserException;

    void remove(User user) throws UserNotFoundException, StorageException;

    void removeByMail(String mail) throws UserNotFoundException, StorageException;

    List<User> getAll() throws StorageException;

    User getByMail(String mail) throws UserNotFoundException, StorageException;

    Optional<Role> getRoleByCredentials(String login, String password) throws StorageException;
}
