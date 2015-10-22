package edu.tsp.asr.asrimbra.repositories.remote;

import edu.tsp.asr.asrimbra.exceptions.StorageException;
import edu.tsp.asr.asrimbra.repositories.api.AbstractUserRepositoryTest;
import edu.tsp.asr.asrimbra.repositories.api.UserRepository;
import org.junit.Ignore;


@Ignore("Those tests needs a running directory manager server (with a clean db) which is not default behaviour")
public class UserRemoteRepositoryTest extends AbstractUserRepositoryTest {
    private static final String DIRECTORY_MANAGER_URL = "http://localhost:7654";

    @Override
    protected UserRepository createUserRepository() {
        UserRemoteRepository userRemoteRepository = new UserRemoteRepository(DIRECTORY_MANAGER_URL);
        try {
            userRemoteRepository.empty();
        } catch (StorageException e) {
            throw new ExceptionInInitializerError();
        }
        return userRemoteRepository;
    }

}
