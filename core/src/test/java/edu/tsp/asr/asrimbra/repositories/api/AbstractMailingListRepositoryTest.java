package edu.tsp.asr.asrimbra.repositories.api;

import edu.tsp.asr.asrimbra.entities.MailingList;
import edu.tsp.asr.asrimbra.exceptions.ExistingMailingListException;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractMailingListRepositoryTest {
    private MailingListRepository mailingListRepository;

    private static final String ADDRESS1 = "test1@example.org";
    private static final String ADDRESS2 = "test2@example.org";
    private static final String ADDRESS3 = "test3@example.org";

    @Before
    public void setUp() throws Exception {
        mailingListRepository = createMailingListRepository();
    }

    @Test(expected = ExistingMailingListException.class)
    public void addShouldThrowIfAddressAlreadyUsed() throws Exception {
        // @todo: implement and test
        throw new ExistingMailingListException();
    }

    @Test
    public void getByAddressShouldReturnEmptyOptionalIfMailingListDoesNotExist() throws Exception {
        assertThat(mailingListRepository.getByAddress(ADDRESS1).isPresent()).isFalse();
    }

    @Test
    public void getByAddressShouldReturnMailingListIfItExists() throws Exception {
        MailingList mailingList = new MailingList(ADDRESS1);
        mailingList.addSubscriberMail(ADDRESS2);
        mailingList.addSubscriberMail(ADDRESS3);
        mailingListRepository.add(mailingList);
        Optional<MailingList> maybeMailingList = mailingListRepository.getByAddress(ADDRESS1);
        assertThat(maybeMailingList.isPresent()).isTrue();
    }

    @Test
    public void getByAddressShouldStoreMailingListValues() throws Exception {
        MailingList mailingList = new MailingList(ADDRESS1);
        mailingList.addSubscriberMail(ADDRESS2);
        mailingList.addSubscriberMail(ADDRESS3);
        mailingListRepository.add(mailingList);
        MailingList storedMailingList = mailingListRepository.getByAddress(ADDRESS1).get();
        assertThat(storedMailingList.getAddress()).isEqualTo(ADDRESS1);
        assertThat(storedMailingList.getSubscribersMails()).containsOnly(ADDRESS2, ADDRESS3);
    }

    @Test
    public void addAndGetByAddressShouldWork() throws Exception {
        mailingListRepository.add(new MailingList(ADDRESS1));
        mailingListRepository.getByAddress(ADDRESS1);
    }

    @Test
    public void removeShouldWork() throws Exception {
        mailingListRepository.add(new MailingList(ADDRESS1));

    }

    protected abstract MailingListRepository createMailingListRepository();
}