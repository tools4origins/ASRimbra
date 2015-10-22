package edu.tsp.asr.asrimbra.repositories.memory;

import edu.tsp.asr.asrimbra.repositories.api.AbstractMailRepositoryTest;
import edu.tsp.asr.asrimbra.repositories.api.AbstractUserRepositoryTest;
import edu.tsp.asr.asrimbra.repositories.api.MailRepository;
import edu.tsp.asr.asrimbra.repositories.api.UserRepository;

public class UserMemoryRepositoryTest extends AbstractUserRepositoryTest {

    @Override
    protected UserRepository createUserRepository() {
        return new UserMemoryRepository();
    }
}
