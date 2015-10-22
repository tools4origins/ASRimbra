package edu.tsp.asr.asrimbra.repositories.jpa;

import edu.tsp.asr.asrimbra.repositories.api.AbstractMailRepositoryTest;
import edu.tsp.asr.asrimbra.repositories.api.AbstractMailingListRepositoryTest;
import edu.tsp.asr.asrimbra.repositories.api.MailRepository;
import edu.tsp.asr.asrimbra.repositories.api.MailingListRepository;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class MailingListJPARepositoryTest extends AbstractMailingListRepositoryTest {
    private static final String HIBERNATE_CONFIG_FILE = "META-INF/testDatabase.cfg.xml";

    @Override
    protected MailingListRepository createMailingListRepository() {
        SessionFactory factory;
        try {
            factory = new Configuration().configure(HIBERNATE_CONFIG_FILE).buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
        return new MailingListJPARepository(factory);
    }
}
