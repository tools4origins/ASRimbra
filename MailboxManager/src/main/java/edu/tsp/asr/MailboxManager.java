package edu.tsp.asr;

import edu.tsp.asr.entities.Mail;
import edu.tsp.asr.entities.MailingList;
import edu.tsp.asr.entities.Role;
import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.MailNotFoundException;
import edu.tsp.asr.exceptions.UserNotFoundException;
import edu.tsp.asr.repositories.MailRepository;
import edu.tsp.asr.repositories.MailingListRepository;
import edu.tsp.asr.repositories.UserRepository;
import edu.tsp.asr.transformers.JsonTransformer;
import spark.ResponseTransformer;

import java.util.Optional;

import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.options;
import static spark.Spark.port;
import static spark.Spark.post;

public class MailboxManager {
    private static final ResponseTransformer transformer = new JsonTransformer();
    private static final String TOKEN_COOKIE_NAME = "token";
    private static final Integer PORT_LISTENED = 4567;

    public static void main(String[] a) {
        // Specifies the port listened by the DirectoryManager
        port(PORT_LISTENED);

        // @todo : assert not null params
        // @todo : use config file
        // Repositories used by the applications
        UserRepository userRepository = new UserRemoteRepository("http://localhost:7654/user/");
        MailRepository mailRepository = new MailMemoryRepository();
        MailingListRepository mailingListMemoryRepository = new MailingListMemoryRepository();

        // Populate repository in order to facilitate tests
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

        before("/mailbox/*", (request, response) -> {
            String token = request.cookie(TOKEN_COOKIE_NAME);
            if (!TokenManager.checkToken(token)) {
                halt(401, "You are not logged in :(");
            }
        });

        get("/mailbox/", (request, response) -> {
            String userMail = TokenManager.extractUserName(request.cookie(TOKEN_COOKIE_NAME));
            System.out.println("userMail");
            System.out.println(userMail);
            return mailRepository.getByUserMail(userMail);
        }, transformer);

        get("/mailbox/disconnect/", (request, response) -> {
            response.removeCookie(TOKEN_COOKIE_NAME);
            return "";
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
            User user = request.session().attribute("user");

            Integer id = 0;
            try {
                id = Integer.parseInt(request.params(":id"));
            } catch (NumberFormatException e) {
                halt(400, "id is not a number");
            }

            try {
                Mail mail = mailRepository.getByUserAndId(user, id);
                mailRepository.remove(mail);
                response.status(204);
                return null;
            } catch (MailNotFoundException e) {
                halt(404, "Mail not found");
                return null;
            }
        }, transformer);

        post("/mailbox/send/", (request, response) -> {
            User user = request.session().attribute("user");

            try {
                assert request.queryParams("to") != null;
                assert request.queryParams("title") != null;
                assert request.queryParams("content") != null;
                String from = user.getMail();
                String to = request.queryParams("to");
                String title = request.queryParams("title");
                String content = request.queryParams("content");
                Mail newMail;

                // Check if the mail is sent to a mailing list
                Optional<MailingList> optional = mailingListMemoryRepository.getByAddress(to);
                if (optional.isPresent()) {
                    // if so, we forward it directly to all of its subscribers
                    MailingList mailingList = optional.get();
                    for (User subscriber : mailingList.getSubscribers()) {
                        // variable to is here the mailingList address
                        newMail = new Mail(to, subscriber.getMail(), title, content);
                        mailRepository.add(newMail);
                    }
                    return mailingList.getSubscribers().size();
                } else {
                    // else we consider that it is a normal mail
                    newMail = new Mail(from, to, title, content);
                    mailRepository.add(newMail);
                    return newMail.getId();
                }

            } catch (NullPointerException e) {
                halt(400, "Bad parameters");
                return null;
            }
        }, transformer);
    }
}
