package edu.tsp.asr;

import edu.tsp.asr.entities.Role;
import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.StorageException;
import edu.tsp.asr.exceptions.UserNotAllowedException;
import edu.tsp.asr.exceptions.UserNotFoundException;
import edu.tsp.asr.repositories.UserRepository;
import edu.tsp.asr.transformers.JsonTransformer;
import spark.ResponseTransformer;


import java.util.List;

import static spark.Spark.*;

public class DirectoryManage {
    // @todo : connexion should be remove as it is no longer used
    public static boolean connexion=false;


    // @todo : forms used on client should be in the same page
    public static void main(String[] a) {
        UserRepository userRepository = new UserMemoryRepository();

        // Populate repository in order to facilitate tests
        // @todo : setAdmin() should be call directly the user
        try {
            userRepository.add(new User("guyomarc@tem-tsp.eu", "passwd"));
            userRepository.add(new User("atilalla@tem-tsp.eu", "passwd2"));
        } catch (StorageException e) {
            e.printStackTrace();
        }
        try {
            User u = userRepository.getByMail("guyomarc@tem-tsp.eu");
            u.setAdmin();
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        } catch (StorageException e) {
            e.printStackTrace();
        }

        // @todo : transformer should be a static attribute
        ResponseTransformer transformer = new JsonTransformer();
        // Specifies the port listened by the DirectoryManager
        port(7654);

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

        post("/connect/", (request, response) -> {
            try {
                User user = userRepository.getByCredentials(
                        request.queryParams("mail"),
                        request.queryParams("password")
                );
                if (user.getRole() == Role.ADMIN) {
                    request.session().attribute("user", user);
                    connexion = true;
                    response.redirect("/user/getAll/");
                } else
                    throw new UserNotAllowedException();

                return "";
            } catch (UserNotFoundException e) {
                halt(403, "Bad credentials :(");
                return "";
            } catch (UserNotAllowedException e) {
                halt(403, "Not allowed :(");
                return "";
            }
        }, transformer);

        before("/user/*", (request, response) -> {
            List<User> users = userRepository.getAll();
            request.session().attribute("user", users.get(0));
            if (request.session().attribute("user") == null) {
                halt(401, "You are not logged in :(");
            }
        });

        // @todo : normalize lambdas parameters
        get("/", (rq, rs) -> {
            return userRepository.getAll();
        }, transformer);

        post("/user/add/", (request, response) -> {
            User user = new User(request.queryParams("email"), request.queryParams("password"));

            // @todo : *Repository.add should throw exception if user already exist instead of current behavior
            try {
                User user1 = userRepository.getByMail(user.getMail());
                return "User registred";
            } catch (UserNotFoundException e) {
                userRepository.add(user);
                return "Added Succefully";
            }

        });

        // @todo: refactor to generalise
        options("/user/removeByEmail", (request, response) -> {
            response.header(
                    "Access-Control-Allow-Methods",
                    "DELETE, OPTIONS"
            );
            return "";
        });

        delete("/user/removeByEmail", (request, response) -> {
            String email;
            email = request.queryParams("email");
            System.out.println(email);

            try {
                User user = userRepository.getByMail(email);
                userRepository.remove(user);
                response.status(204);
            } catch (UserNotFoundException e) {
                halt(404, "User not found");
                return null;
            }
            return "Removed Succefully";

        }, transformer);

        get("/user/getRight", (request, response) -> {
            User user = userRepository.getByMail(request.queryParams("user"));
            return user.getRole();

        }, transformer);

        post("/user/setRight", (request, response) -> {
            User user = userRepository.getByMail(request.queryParams("email"));
            user.setAdmin();
            return "Updates Succefully";
        });

        get("/user/getByMail", (request, response) -> {
            return userRepository.getByMail(request.queryParams("email"));
        }, transformer);

        get("/user/getAll", (request, response) -> {
            return userRepository.getAll();
        }, transformer);

        get("/disconnect/", (request, response) -> {
            request.session().removeAttribute("user");
            connexion = false;
            return "";
        }, transformer);

        // @todo : consider using exception (spark framework style)
        // @todo : set status code in previous code
        // @todo : correct english typo in previous code
        // @todo : implement missing route or remove use of them (add for example)
    }

}
