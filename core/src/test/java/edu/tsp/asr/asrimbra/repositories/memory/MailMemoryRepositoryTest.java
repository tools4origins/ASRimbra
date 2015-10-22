package edu.tsp.asr.asrimbra.repositories.memory;

import edu.tsp.asr.asrimbra.repositories.api.AbstractMailRepositoryTest;
import edu.tsp.asr.asrimbra.repositories.api.MailRepository;

public class MailMemoryRepositoryTest extends AbstractMailRepositoryTest {

    @Override
    protected MailRepository createMailRepository() {
        return new MailMemoryRepository();
    }
}
