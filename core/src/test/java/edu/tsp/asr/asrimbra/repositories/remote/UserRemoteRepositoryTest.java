package edu.tsp.asr.asrimbra.repositories.remote;

import edu.tsp.asr.asrimbra.repositories.api.AbstractUserRepositoryTest;
import edu.tsp.asr.asrimbra.repositories.api.UserRepository;
import org.junit.Ignore;


@Ignore("Those tests needs a running directory manager server which is not default behaviour")
public class UserRemoteRepositoryTest extends AbstractUserRepositoryTest {
    private static final String DIRECTORY_MANAGER_URL = "http://localhost:7654";

    @Override
    protected UserRepository createUserRepository() {
        return new UserRemoteRepository(DIRECTORY_MANAGER_URL);
        // @todo : use a mock so it remains an integration test
    }
}
