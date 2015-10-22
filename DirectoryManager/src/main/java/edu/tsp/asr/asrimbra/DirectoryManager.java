package edu.tsp.asr.asrimbra;

import com.goebl.david.Request;
import edu.tsp.asr.asrimbra.entities.Role;
import edu.tsp.asr.asrimbra.entities.User;
import edu.tsp.asr.asrimbra.exceptions.ExistingUserException;
import edu.tsp.asr.asrimbra.exceptions.StorageException;
import edu.tsp.asr.asrimbra.helpers.RemoteHelper;
import edu.tsp.asr.asrimbra.helpers.SparkHelper;
import edu.tsp.asr.asrimbra.helpers.TokenHelper;
import edu.tsp.asr.asrimbra.repositories.api.UserRepository;
import edu.tsp.asr.asrimbra.repositories.jpa.UserJPARepository;
import edu.tsp.asr.asrimbra.transformers.JsonTransformer;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import spark.ResponseTransformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private static String MAILBOX_MANAGER_URI =  "http://localhost:4567";
    private static String DIRECTORY_MANAGER_ACCOUNT = "root";

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
            User u1 = new User("atilalla@tem-tsp.eu", "passwd");
            User u2 = new User("guyomarc@tem-tsp.eu", "passwd2");
            u1.setAdmin();
            u2.setAdmin();
            userRepository.add(u1);
            userRepository.add(u2);
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
            String userMail = request.params("mail");
            System.out.println("user: "+userMail);
            userRepository.removeByMail(userMail);

            // Remove mails associated to userMail
            Map<String, Object> params = new HashMap<>();
            params.put("token", TokenHelper.get(DIRECTORY_MANAGER_ACCOUNT));
            params.put("mail", userMail);
            RemoteHelper.getRemoteString(MAILBOX_MANAGER_URI + "/admin/removeByUserMail", Request.Method.POST, params);
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
            SparkHelper.checkQueryParamsNullity(request, "mail");
            return userRepository.getByMail(request.queryParams("mail"));
        }, transformer);

        get("/user/getAll", (request, response) -> {
            return userRepository.getAll();
        }, transformer);

        // facilitate tests, you may want to remove it in prod since it is quite dangerous
        // currently there is no propagation, by choice, since it is for tests & dangerous
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