package edu.tsp.asr;

import edu.tsp.asr.entities.Role;
import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.ExistingUserException;
import edu.tsp.asr.exceptions.StorageException;
import edu.tsp.asr.exceptions.UserNotAllowedException;
import edu.tsp.asr.exceptions.UserNotFoundException;
import edu.tsp.asr.repositories.UserRepository;
import edu.tsp.asr.transformers.JsonTransformer;
import spark.ResponseTransformer;

import java.util.Optional;

import static spark.Spark.*;

public class DirectoryManage {

    private static final ResponseTransformer transformer = new JsonTransformer();
    private static final String TOKEN_COOKIE_NAME = "token";
    private static final Integer PORT_LISTENED = 7654;

    // @todo : create gateway
    // @todo : forms used on client should be in the same page
    public static void main(String[] a) {
        // Specifies the port listened by the DirectoryManager
        port(PORT_LISTENED);

        // Repositories used by the applications
        UserRepository userRepository = new UserMemoryRepository();

        // Populate repository in order to facilitate tests
        try {
            User u = new User("guyomarc@tem-tsp.eu", "passwd");
            u.setAdmin();
            userRepository.add(u);
            userRepository.add(new User("atilalla@tem-tsp.eu", "passwd2"));
        } catch (StorageException | ExistingUserException e) {
            e.printStackTrace();
        }

        //Allow Cross-origin resource sharing
        before(((request, response) -> {
            response.header(
                    "Access-Control-Allow-Origin",
                    request.headers("Origin")
            );
            response.header(
                    "Access-Control-Allow-Credentials",
                    "true"
            );
        }));

        post("/connect", (request, response) -> {
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
        get("/", (request, response) -> {
            return userRepository.getAll();
        }, transformer);

        post("/user/add", (request, response) -> {
            User user = new User(request.queryParams("email"), request.queryParams("password"));
            try {
                userRepository.add(user);
            } catch (ExistingUserException e) {
                halt(400, "User already exist");
            }
            return user.getId();
        });

        // @todo: refactor to generalise
        options("/user/removeByEmail/:email", (request, response) -> {
            response.header(
                    "Access-Control-Allow-Methods",
                    "DELETE, OPTIONS"
            );
            response.status(200);
            return "";
        });

        delete("/user/removeByEmail/:email", (request, response) -> {
            String email = request.params(":email");
            try {
                User user = userRepository.getByMail(email);
                userRepository.remove(user);
                response.status(204);
            } catch (UserNotFoundException e) {
                halt(404, "User not found");
            }
            return "";
        }, transformer);

        get("/user/getRight", (request, response) -> {
            User user = userRepository.getByMail(request.queryParams("user"));
            return user.getRole();
        }, transformer);

        post("/user/setRight", (request, response) -> {
            User user = userRepository.getByMail(request.queryParams("email"));
            user.setAdmin();
            response.status(204);
            return "";
        });

        get("/user/getByMail", (request, response) -> {
            return userRepository.getByMail(request.queryParams("email"));
        }, transformer);

        get("/user/getAll", (request, response) -> {
            return userRepository.getAll();
        }, transformer);

        get("/user/getRoleByCredentials", (request, response) -> {
            return userRepository.getRoleByCredentials(request.queryParams("login"), request.queryParams("password"));
        }, transformer);

        get("/disconnect", (request, response) -> {
            response.removeCookie(TOKEN_COOKIE_NAME);
            return "";
        }, transformer);
    }
}