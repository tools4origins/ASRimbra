package edu.tsp.asr.asrimbra;

import edu.tsp.asr.asrimbra.entities.Role;
import edu.tsp.asr.asrimbra.entities.User;
import edu.tsp.asr.asrimbra.exceptions.ExistingUserException;
import edu.tsp.asr.asrimbra.exceptions.StorageException;
import edu.tsp.asr.asrimbra.repositories.api.UserRepository;
import edu.tsp.asr.asrimbra.repositories.jpa.UserJPARepository;
import edu.tsp.asr.asrimbra.transformers.JsonTransformer;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import spark.ResponseTransformer;

import java.util.List;

import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.options;
import static spark.Spark.port;
import static spark.Spark.post;

public class DirectoryManager {

    private static final ResponseTransformer transformer = new JsonTransformer();
    private static final Integer PORT_LISTENED = 7654;
    private static final String HIBERNATE_CONFIG_FILE = "META-INF/hibernateDirectoryManager.cfg.xml";

    // @todo : forms used on edu.tsp.asr.asrimbra.client should be in the same page
    // @todo: merge forms, all should use mail and not email.
    // @todo : deleting user should remove its mails
    public static void main(String[] a) {
        // Specifies the port listened by the DirectoryManager
        port(PORT_LISTENED);

        // Repositories used by the applications
        SessionFactory factory;
        try {
            factory = new Configuration().configure(HIBERNATE_CONFIG_FILE).buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
        UserRepository userRepository = new UserJPARepository(factory);

        // Populate repository in order to facilitate tests
        try {
            User u = new User("guyomarc@tem-tsp.eu", "passwd");
            u.setAdmin();
            userRepository.add(u);
            userRepository.add(new User("atilalla@tem-tsp.eu", "passwd2"));
        } catch (StorageException | ExistingUserException e) {
            e.printStackTrace();
        }

        // Allow Cross-origin resource sharing
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

        post("/user/addByCredentials", (request, response) -> {
            SparkHelper.checkQueryParamsNullity(request, "mail", "password");

            User user = new User(request.queryParams("mail"), request.queryParams("password"));
            try {
                userRepository.add(user);
            } catch (ExistingUserException e) {
                halt(400, "User already exist");
            }
            return "";
        }, transformer);

        post("/user/add", (request, response) -> {
            SparkHelper.checkQueryParamsNullity(request, "mail", "passwordHash", "role");

            try {
                User user = new User();
                user.setMail(request.queryParams("mail"));
                user.setPasswordHash(request.queryParams("passwordHash"));
                user.setRole(Role.valueOf(request.queryParams("role")));
                userRepository.add(user);
            } catch (ExistingUserException e) {
                halt(402, "User already exist");
            }

            return "";
        }, transformer);

        options("/user/removeByMail/:mail", SparkHelper.generateOptionRoute("DELETE"));

        delete("/user/removeByMail/:mail", (request, response) -> {
            String userMail = request.queryParams("mail");
            userRepository.removeByMail(userMail);
            response.status(202);
            return "";
        }, transformer);

        get("/user/getRight", (request, response) -> {
            SparkHelper.checkQueryParamsNullity(request, "mail");
            User user = userRepository.getByMail(request.queryParams("mail"));
            return user.getRole();
        }, transformer);

        post("/user/setAdmin", (request, response) -> {
            SparkHelper.checkQueryParamsNullity(request, "mail");
            userRepository.setAdmin(request.queryParams("mail"));
            response.status(204);
            return "";
        });

        post("/user/setSimpleUser", (request, response) -> {
            SparkHelper.checkQueryParamsNullity(request, "mail");
            userRepository.setSimpleUser(request.queryParams("mail"));
            response.status(204);
            return "";
        });

        get("/user/getByMail", (request, response) -> {
            System.out.println("a");
            SparkHelper.checkQueryParamsNullity(request, "mail");
            System.out.println("b");
            return userRepository.getByMail(request.queryParams("mail"));
        }, transformer);

        get("/user/getAll", (request, response) -> {
            return userRepository.getAll();
        }, transformer);

        // facilitate tests, should be removed in prod since it is quite dangerous
        get("/user/empty", (request, response) -> {
            List<User> users = userRepository.getAll();
            users.stream()
                    .forEach(u -> {
                        try {
                            userRepository.removeByMail(u.getMail());
                        } catch (StorageException e) {
                            e.printStackTrace();
                        }
                    });
            return "";
        }, transformer);

        get("/user/getRoleByCredentials", (request, response) -> {
            SparkHelper.checkQueryParamsNullity(request, "login", "password");
            return userRepository.getRoleByCredentials(request.queryParams("login"), request.queryParams("password"));
        }, transformer);

    }
}