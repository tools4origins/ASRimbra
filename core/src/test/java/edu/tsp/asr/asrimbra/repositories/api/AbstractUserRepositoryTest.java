package edu.tsp.asr.asrimbra.repositories.api;

import edu.tsp.asr.asrimbra.entities.Role;
import edu.tsp.asr.asrimbra.entities.User;
import edu.tsp.asr.asrimbra.exceptions.ExistingUserException;
import edu.tsp.asr.asrimbra.exceptions.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractUserRepositoryTest {
    private UserRepository userRepository;

    private static final String ADDRESS1 = "test1@example.org";
    private static final String ADDRESS2 = "test2@example.org";
    private static final String PASSWORD1 = "toto";
    private static final String PASSWORD2 = "tutu";
    private static final String PASSWORD3 = "tutu";

    @Before
    public void setUp() throws Exception {
        userRepository = createUserRepository();
    }

    @Test
    public void addAndGetAllShouldWork() throws Exception {
        User user1 = new User(ADDRESS1, PASSWORD1);
        User user2 = new User(ADDRESS2, PASSWORD2);
        userRepository.add(user1);
        userRepository.add(user2);
        assertThat(userRepository.getAll()).containsOnly(user1, user2);
    }

    @Test(expected = ExistingUserException.class)
    public void addShouldThrowIfUserAlreadyExist() throws Exception {
        userRepository.add(new User(ADDRESS1, PASSWORD1));
        userRepository.add(new User(ADDRESS1, PASSWORD2));
    }

    @Test()
    public void removeByMailShouldNotThrowIfUserDoesNotExist() throws Exception {
        userRepository.removeByMail(ADDRESS1);
    }

    @Test(expected = UserNotFoundException.class)
    public void removeByMailShouldWork() throws Exception {
        userRepository.add(new User(ADDRESS1, PASSWORD1));
        userRepository.removeByMail(ADDRESS1);
        userRepository.getByMail(ADDRESS1);
    }

    @Test(expected = UserNotFoundException.class)
    public void getByMailShouldThrowIfUserDoesNotExist() throws Exception {
        userRepository.getByMail(ADDRESS1);
    }

    @Test
    public void getByMailShouldWork() throws Exception {
        userRepository.add(new User(ADDRESS1, PASSWORD1));
        userRepository.getByMail(ADDRESS1);
    }

    @Test
    public void getRoleByCredentialsShouldReturnEmptyOptionalIfUserDoesNotExist() throws Exception {
        assertThat(userRepository.getRoleByCredentials(ADDRESS1, PASSWORD1).isPresent()).isFalse();
    }

    @Test
    public void getRoleByCredentialsShouldWorkOnSimpleUser() throws Exception {
        userRepository.add(new User(ADDRESS1, PASSWORD1));
        assertThat(userRepository.getRoleByCredentials(ADDRESS1, PASSWORD1).get()).isEqualTo(Role.USER);
    }

    @Test
    public void getRoleByCredentialsShouldWorkOnAdmin() throws Exception {
        User user = new User(ADDRESS1, PASSWORD1);
        user.setAdmin();
        userRepository.add(user);
        assertThat(userRepository.getRoleByCredentials(ADDRESS1, PASSWORD1).get()).isEqualTo(Role.ADMIN);
    }

    protected abstract UserRepository createUserRepository();
}