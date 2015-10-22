package edu.tsp.asr.asrimbra;

import edu.tsp.asr.asrimbra.entities.Role;
import edu.tsp.asr.asrimbra.entities.User;
import edu.tsp.asr.asrimbra.exceptions.ExistingUserException;
import edu.tsp.asr.asrimbra.exceptions.StorageException;
import edu.tsp.asr.asrimbra.exceptions.UserNotAllowedException;
import edu.tsp.asr.asrimbra.exceptions.UserNotFoundException;
import edu.tsp.asr.asrimbra.repositories.api.UserRepository;
import edu.tsp.asr.asrimbra.repositories.jpa.UserJPARepository;
import edu.tsp.asr.asrimbra.transformers.JsonTransformer;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.json.JSONObject;
import spark.ResponseTransformer;

import java.util.Optional;

import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.options;
import static spark.Spark.port;
import static spark.Spark.post;

public class DirectoryManager {

    private static final ResponseTransformer transformer = new JsonTransformer();
    private static final String TOKEN_COOKIE_NAME = "token";
    private static final Integer PORT_LISTENED = 7654;
    private static final String HIBERNATE_CONFIG_FILE = "META-INF/hibernateDirectoryManager.cfg.xml";

    // @todo : create gateway
    // @todo : forms used on client should be in the same page
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

        post("/connect", (request, response) -> {
            SparkHelper.checkQueryParamsNullity(request, "mail", "password");

            try {
                Optional<Role> role = userRepository.getRoleByCredentials(
                        request.queryParams("mail"),
                        request.queryParams("password")
                );

                if (!role.isPresent()) {
                    throw new UserNotFoundException();
                } else if (role.get() != Role.ADMIN) {
                    throw new UserNotAllowedException();
                }
                response.cookie(TOKEN_COOKIE_NAME, TokenManager.get(request.queryParams("mail")));
            } catch (UserNotFoundException e) {
                halt(403, "Bad credentials :(");
            } catch (UserNotAllowedException e) {
                halt(403, "Not allowed :(");
            }
            return "";
        }, transformer);
/*
        before("/user/*", (request, response) -> {
            String token = request.cookie(TOKEN_COOKIE_NAME);
            if (!TokenManager.checkToken(token)) {
                halt(401, "You are not logged in :(");
            }
        });
*/

        post("/user/add", (request, response) -> {
            SparkHelper.checkQueryParamsNullity(request, "mail", "password");

            User user = new User(request.queryParams("mail"), request.queryParams("password"));
            try {
                userRepository.add(user);
            } catch (ExistingUserException e) {
                halt(400, "User already exist");
            }
            return "";
        });

        post("/user/add", (request, response) -> {
            SparkHelper.checkQueryParamsNullity(request, "user");

            JSONObject jsonUser = new JSONObject(request.queryParams("user"));
            User user = JSONHelper.JSONToUser(jsonUser);

            try {
                userRepository.add(user);
            } catch (ExistingUserException e) {
                halt(400, "User already exist");
            }
            return "";
        });

        options("/user/removeByMail/:mail", SparkHelper.generateOptionRoute("DELETE"));

        delete("/user/removeByMail/:mail", (request, response) -> {
            SparkHelper.checkQueryParamsNullity(request, "mail");

            String userMail = request.queryParams("mail");
            userRepository.removeByMail(userMail);
            response.status(202);
            return "";
        }, transformer);

        get("/user/getRight", (request, response) -> {
            SparkHelper.checkQueryParamsNullity(request, "user");
            User user = userRepository.getByMail(request.queryParams("user"));
            return user.getRole();
        }, transformer);

        post("/user/setRight", (request, response) -> {
            SparkHelper.checkQueryParamsNullity(request, "mail");
            User user = userRepository.getByMail(request.queryParams("mail"));
            user.setAdmin();
            response.status(204);
            return "";
        });

        get("/user/getByMail", (request, response) -> {
            SparkHelper.checkQueryParamsNullity(request, "mail");
            return userRepository.getByMail(request.queryParams("email"));
        }, transformer);

        get("/user/getAll", (request, response) -> {
            return userRepository.getAll();
        }, transformer);

        get("/user/getRoleByCredentials", (request, response) -> {
            SparkHelper.checkQueryParamsNullity(request, "login", "password");
            return userRepository.getRoleByCredentials(request.queryParams("login"), request.queryParams("password"));
        }, transformer);

        get("/disconnect", (request, response) -> {
            response.removeCookie(TOKEN_COOKIE_NAME);
            return "";
        }, transformer);
    }
}