package edu.tsp.asr;

import edu.tsp.asr.entities.Role;
import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.StorageException;
import edu.tsp.asr.exceptions.UserNotAllowedException;
import edu.tsp.asr.exceptions.UserNotFoundException;
import edu.tsp.asr.repositories.UserRepository;
import edu.tsp.asr.transformers.JsonTransformer;
import spark.ResponseTransformer;

import static spark.Spark.*;

/**
 * Created by atitalla on 12/10/15.
 */
public class DirectoryManage {
    public static void main(String[] a) {
        UserRepository userRepository = new UserMemoryRepository();

        try {
            userRepository.addUser(new User("guyomarc@tem-tsp.eu", "passwd"));
            userRepository.addUser(new User("atilalla@tem-tsp.eu", "passwd2"));
        } catch (StorageException e) {
            e.printStackTrace();
        }
        ResponseTransformer transformer = new JsonTransformer();
        System.out.println("Directory");
        port(7654);
        get("/", (rq, rs) -> userRepository.getAllUsers(),transformer);

        post("/user/addUser/", (request, response) -> {
            User user = new User(request.queryParams("email"), request.queryParams("password"));
            userRepository.addUser(user);
            return "Added Succefully";
        });

        delete("/user/removeUser", (request, response) -> {
            System.out.println("Hi");
            User user = userRepository.getUserByMail(request.queryParams("email"));
            System.out.println(user.getMail());
            userRepository.removeUser(user);
            return "Removed Succefully";
        });

        get("/user/right/", (request, response) -> {
            User user = userRepository.getUserByMail(request.queryParams("user"));
            return user.getRole();
        },transformer);

        post("/user/right/update/", (request, response) -> {
            User user = userRepository.getUserByMail(request.queryParams("email"));
            user.setAdmin();
            return "Updates Succefully";
        });

        get("/user/getUserByMail", (request, response) -> {
            User user = userRepository.getUserByMail(request.queryParams("email"));
            return user;
        },transformer);

        get("/user/getAllUsers", (request, response) -> {
            return userRepository.getAllUsers();
        },transformer);

        post("/connect/", (request, response) -> {
            try {
                User user = userRepository.getUserByCredentials(
                        request.queryParams("mail"),
                        request.queryParams("password")
                );
                if (user.getRole() == Role.ADMIN) {
                    request.session().attribute("user", user);
                    response.redirect("/user/all/");
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

    }
}
