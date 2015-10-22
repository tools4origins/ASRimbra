package edu.tsp.asr.asrimbra.repositories.jpa;

import edu.tsp.asr.asrimbra.repositories.api.AbstractMailRepositoryTest;
import edu.tsp.asr.asrimbra.repositories.api.MailRepository;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class MailJPARepositoryTest extends AbstractMailRepositoryTest {
    private static final String HIBERNATE_CONFIG_FILE = "META-INF/testDatabase.cfg.xml";

    @Override
    protected MailRepository createMailRepository() {
        SessionFactory factory;
        try {
            factory = new Configuration().configure(HIBERNATE_CONFIG_FILE).buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
        return new MailJPARepository(factory);
    }
}
