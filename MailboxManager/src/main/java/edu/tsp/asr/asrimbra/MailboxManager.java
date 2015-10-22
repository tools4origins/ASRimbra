package edu.tsp.asr.asrimbra;

import edu.tsp.asr.asrimbra.entities.Mail;
import edu.tsp.asr.asrimbra.entities.MailingList;
import edu.tsp.asr.asrimbra.entities.Role;
import edu.tsp.asr.asrimbra.entities.User;
import edu.tsp.asr.asrimbra.exceptions.MailNotFoundException;
import edu.tsp.asr.asrimbra.exceptions.StorageException;

import edu.tsp.asr.asrimbra.repositories.api.MailRepository;
import edu.tsp.asr.asrimbra.repositories.api.MailingListRepository;
import edu.tsp.asr.asrimbra.repositories.api.UserRepository;
import edu.tsp.asr.asrimbra.repositories.jpa.MailJPARepository;
import edu.tsp.asr.asrimbra.repositories.jpa.MailingListJPARepository;
import edu.tsp.asr.asrimbra.repositories.remote.UserRemoteRepository;
import edu.tsp.asr.asrimbra.transformers.JsonTransformer;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import spark.ResponseTransformer;

import java.util.Optional;

import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.port;
import static spark.Spark.post;

public class MailboxManager {
    private static ResponseTransformer transformer = new JsonTransformer();
    private static String TOKEN_COOKIE_NAME = "token";
    private static Integer PORT_LISTENED = 4567;
    private static String HIBERNATE_CONFIG_FILE = "META-INF/hibernateMailboxManager.cfg.xml";
    private static String DIRECTORY_MANAGER_URL = "http://localhost:7654/user/";

    public static void main(String[] a) {
        // @todo: read config -> http://www.mkyong.com/java/java-properties-file-examples/


        SessionFactory factory;
        try {
            factory = new Configuration().configure(HIBERNATE_CONFIG_FILE).buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }

        // Repositories used by the applications
        UserRepository userRepository = new UserRemoteRepository(DIRECTORY_MANAGER_URL);
        MailRepository mailRepository = new MailJPARepository(factory);
        MailingListRepository mailingListMemoryRepository = new MailingListJPARepository(factory);

        // Populate repository in order to facilitate tests
        try {
            mailRepository.add(
                    new Mail(
                            "guyomarc@tem-tsp.eu",
                            "atilalla@tem-tsp.eu",
                            "title",
                            "content"
                    )
            );
            mailRepository.add(
                    new Mail(
                            "atilalla@tem-tsp.eu",
                            "guyomarc@tem-tsp.eu",
                            "title2",
                            "content2"
                    )
            );
        } catch (StorageException e) {
            e.printStackTrace();
        }

        // Specifies the port listened by the MailboxManager
        port(PORT_LISTENED);

        //Allow Cross-origin resource sharing
        before((request, response) -> {
            response.header(
                    "Access-Control-Allow-Origin",
                    request.headers("Origin")
            );
            response.header(
                    "Access-Control-Allow-Credentials",
                    "true"
            );
        });

        post("/connect", (request, response) -> {
            SparkHelper.checkQueryParamsNullity(request, "mail", "password");

            Optional<Role> opt = userRepository.getRoleByCredentials(
                    request.queryParams("mail"),
                    request.queryParams("password")
            );

            if (opt.isPresent()) {
                response.cookie(TOKEN_COOKIE_NAME, TokenManager.get(request.queryParams("mail")));
                return "";
            } else {
                halt(403, "Bad credentials :(");
                return "";
            }
        }, transformer);

        get("/disconnect", (request, response) -> {
            response.removeCookie(TOKEN_COOKIE_NAME);
            return "";
        }, transformer);

        before("/mailbox/*", (request, response) -> {
            String token = request.cookie(TOKEN_COOKIE_NAME);
            if (!TokenManager.checkToken(token)) {
                halt(401, "You are not logged in :(");
            }
        });

        get("/mailbox/", (request, response) -> {
            String userMail = TokenManager.extractUserName(request.cookie(TOKEN_COOKIE_NAME));
            return mailRepository.getByUserMail(userMail);
        }, transformer);


        get("/mailbox/:id", (request, response) -> {
            try {
                Integer id = Integer.parseInt(request.params(":id"));
                String userMail = TokenManager.extractUserName(request.cookie(TOKEN_COOKIE_NAME));
                return mailRepository.getByUserMailAndId(userMail, id);
            } catch (MailNotFoundException e) {
                halt(404, "Mail not found");
                return "";
            } catch (NumberFormatException e) {
                halt(400, "id is not a number");
                return "";
            }
        }, transformer);

        delete("/mailbox/:id", (request, response) -> {
            Integer id = 0;
            try {
                id = Integer.parseInt(request.params(":id"));
                String userMail = TokenManager.extractUserName(request.cookie("token"));
                mailRepository.removeByUserMailAndId(userMail, id);
                response.status(204);
            } catch (NumberFormatException e) {
                halt(400, "id is not a number");
            }
            response.status(202); // No garanty of success.
            return "";
        }, transformer);

        post("/mailbox/send/", (request, response) -> {
            SparkHelper.checkQueryParamsNullity(request, "to", "title", "content");

            String from = TokenManager.extractUserName(request.cookie("token"));
            String to = request.queryParams("to");
            String title = request.queryParams("title");
            String content = request.queryParams("content");
            Mail newMail;

            // Check if the mail is sent to a mailing list
            Optional<MailingList> maybeMailingList = mailingListMemoryRepository.getByAddress(to);
            if (maybeMailingList.isPresent()) {
                // if so, we forward it directly to all of its subscribers
                MailingList mailingList = maybeMailingList.get();
                for (String subscriberMail : mailingList.getSubscribersMails()) {
                    // variable "to" is the mailingList address
                    newMail = new Mail(to, subscriberMail, title, content);
                    mailRepository.add(newMail);
                }
                return mailingList.getSubscribersMails().size();
            } else {
                // else we consider that it is a normal mail
                newMail = new Mail(from, to, title, content);
                mailRepository.add(newMail);
                return newMail.getId();
            }
        }, transformer);
    }
}
