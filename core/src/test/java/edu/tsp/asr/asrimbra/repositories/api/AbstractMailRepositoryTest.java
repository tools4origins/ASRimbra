package edu.tsp.asr.asrimbra.repositories.api;

import edu.tsp.asr.asrimbra.entities.Mail;
import edu.tsp.asr.asrimbra.exceptions.MailNotFoundException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractMailRepositoryTest {
    private MailRepository mailRepository;

    private static final String ADDRESS1 = "test1@example.org";
    private static final String ADDRESS2 = "test2@example.org";
    private static final String ADDRESS3 = "test3@example.org";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";

    @Before
    public void setUp() throws Exception {
        mailRepository = createMailRepository();
    }

    @Test
    public void addShouldModifyMailId() throws Exception {
        Mail mail = new Mail(ADDRESS1, ADDRESS2, TITLE, CONTENT);
        mailRepository.add(mail);
        assertThat(mail.getId()).isNotNull();
    }

    @Test
    public void getByUserMailShouldWork() throws Exception {
        try {
            Mail mail1to2 = new Mail(ADDRESS1, ADDRESS2, TITLE, CONTENT);
            Mail mail3to1 = new Mail(ADDRESS3, ADDRESS1, TITLE, CONTENT);
            Mail mail3to2 = new Mail(ADDRESS3, ADDRESS2, TITLE, CONTENT);
            mailRepository.add(mail1to2);
            mailRepository.add(mail3to1);
            mailRepository.add(mail3to2);
            System.out.println("Here:");
            mailRepository.getByUserMail(ADDRESS1).stream()
                    .map(m -> "from: " + m.getFrom() + " to: " + m.getTo())
                .forEach(System.out::println);
            assertThat(mailRepository.getByUserMail(ADDRESS1)).containsOnly(mail1to2, mail3to1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    @Test(expected = MailNotFoundException.class)
    public void getByUserMailAndIdShouldThrowIfBadId() throws Exception {
        Mail mail = new Mail(ADDRESS1, ADDRESS2, TITLE, CONTENT);
        mailRepository.add(mail);
        mailRepository.getByUserMailAndId(ADDRESS1, mail.getId() + 1);
    }

    @Test(expected = MailNotFoundException.class)
    public void getByUserMailAndIdShouldThrowIfBadUserMail() throws Exception {
        Mail mail = new Mail(ADDRESS1, ADDRESS2, TITLE, CONTENT);
        mailRepository.add(mail);
        mailRepository.getByUserMailAndId(ADDRESS3, mail.getId());
    }

    @Test
    public void getByUserMailAndIdShouldReturnMailSent() throws Exception {
        Mail mail = new Mail(ADDRESS1, ADDRESS2, TITLE, CONTENT);
        mailRepository.add(mail);
        mailRepository.getByUserMailAndId(ADDRESS1, mail.getId());
    }

    @Test
    public void getByUserMailAndIdShouldReturnMailReceived() throws Exception {
        Mail mail = new Mail(ADDRESS1, ADDRESS2, TITLE, CONTENT);
        mailRepository.add(mail);
        mailRepository.getByUserMailAndId(ADDRESS2, mail.getId());
    }

    @Test(expected = MailNotFoundException.class)
    public void removeShouldWorkOnReceiver() throws Exception {
        Mail mail = new Mail(ADDRESS1, ADDRESS2, TITLE, CONTENT);
        mailRepository.add(mail);
        Integer mailId = mail.getId();
        mailRepository.removeByUserMailAndId(ADDRESS1, mailId);
        mailRepository.getByUserMailAndId(ADDRESS1, mailId);
    }

    @Test(expected = MailNotFoundException.class)
    public void removeShouldWorkOnSender() throws Exception {
        Mail mail = new Mail(ADDRESS1, ADDRESS2, TITLE, CONTENT);
        mailRepository.add(mail);
        Integer mailId = mail.getId();
        mailRepository.removeByUserMailAndId(ADDRESS2, mailId);
        mailRepository.getByUserMailAndId(ADDRESS1, mailId);
    }

    protected abstract MailRepository createMailRepository();
}