package edu.tsp.asr;

import edu.tsp.asr.entities.Mail;
import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.MailNotFoundException;
import edu.tsp.asr.exceptions.UserNotFoundException;
import edu.tsp.asr.repositories.MailRepository;
import edu.tsp.asr.repositories.UserRepository;
import edu.tsp.asr.transformers.JsonTransformer;
import spark.ResponseTransformer;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;

public class MailboxManager {
    public static void main(String[] a) {
        // config
        UserRepository userRepository = new UserMemoryRepository();
        MailRepository mailRepository = new MailMemoryRepository();
        ResponseTransformer transformer = new JsonTransformer();

        // Populate repository
        userRepository.addUser(new User("guyomarc@tem-tsp.eu", "passwd"));
        userRepository.addUser(new User("atilalla@tem-tsp.eu", "passwd2"));
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

        get("/", (request, response) -> userRepository.getAllUsers(), transformer);

        post("/connect/", (request, response) -> {
            try {
                User user = userRepository.getUserByCredentials(
                        request.queryParams("mail"),
                        request.queryParams("password")
                );
                request.session().attribute("user", user);
                response.redirect("/mailbox/");
                return "";
            } catch (UserNotFoundException e) {
                halt(403, "Bad credentials :(");
                return "";
            }
        }, transformer);

        before("/mailbox/*", (request, response) -> {
            if (request.session().attribute("user") == null) {
                halt(401, "You are not logged in :(");
            }
        });

        get("/mailbox/", (request, response) -> {
            User user = request.session().attribute("user");
            return mailRepository.getMailByUser(user);
        }, transformer);


        get("/mailbox/disconnect/", (request, response) -> {
            request.session().removeAttribute("user");
            return "";
        }, transformer);

        get("/mailbox/:id", (request, response) -> {
            User user = request.session().attribute("user");

            Integer id = 0;
            try {
                id = Integer.parseInt(request.params(":id"));
            } catch (NumberFormatException e) {
                halt(400, "id is not a number");
            }

            try {
                return mailRepository.getMailByUserAndId(user, id);
            } catch (MailNotFoundException e) {
                halt(404, "Mail not found");
                return null;
            }
        }, transformer);

        post("/mailbox/send/", (request, response) -> {
            User user = request.session().attribute("user");

            try {
                assert (request.queryParams("to") != null);
                assert (request.queryParams("title") != null);
                assert (request.queryParams("content") != null);
                String from = user.getMail();
                String to = request.queryParams("to");
                String title = request.queryParams("title");
                String content = request.queryParams("content");
                Mail mail = new Mail(from, to, title, content);
                mailRepository.add(mail);
                return mail.getId();
            } catch (NullPointerException e) {
                halt(400, "Bad parameters");
                return null;
            }
        }, transformer);

        }
    }
