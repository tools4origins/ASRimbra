package edu.tsp.asr.asrimbra.repositories.memory;

import edu.tsp.asr.asrimbra.entities.MailingList;
import edu.tsp.asr.asrimbra.repositories.api.AbstractMailRepositoryTest;
import edu.tsp.asr.asrimbra.repositories.api.AbstractMailingListRepositoryTest;
import edu.tsp.asr.asrimbra.repositories.api.MailRepository;
import edu.tsp.asr.asrimbra.repositories.api.MailingListRepository;

public class MailingListMemoryRepositoryTest extends AbstractMailingListRepositoryTest {

    @Override
    protected MailingListRepository createMailingListRepository() {
        return new MailingListMemoryRepository();
    }
}
