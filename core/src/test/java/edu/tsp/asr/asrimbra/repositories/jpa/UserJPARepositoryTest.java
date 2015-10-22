package edu.tsp.asr.asrimbra.repositories.jpa;

import edu.tsp.asr.asrimbra.repositories.api.AbstractUserRepositoryTest;
import edu.tsp.asr.asrimbra.repositories.api.UserRepository;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Ignore;

public class UserJPARepositoryTest extends AbstractUserRepositoryTest {
    private static final String HIBERNATE_CONFIG_FILE = "META-INF/testDatabase.cfg.xml";

    @Override
    protected UserRepository createUserRepository() {
        SessionFactory factory;
        try {
            factory = new Configuration().configure(HIBERNATE_CONFIG_FILE).buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
        return new UserJPARepository(factory);
    }
}
