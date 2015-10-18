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
    public static boolean connexion=false;



    public static void main(String[] a) {
        UserRepository userRepository = new UserMemoryRepository();

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
        ResponseTransformer transformer = new JsonTransformer();
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
            System.out.println("Here we are");
            System.out.println(users.size());
            request.session().attribute("user", users.get(0));
            if (request.session().attribute("user") == null) {
                halt(401, "You are not logged in :(");
            }
        });
        get("/", (rq, rs) -> {
            return userRepository.getAll();

        }, transformer);


        post("/user/add/", (request, response) -> {
            User user = new User(request.queryParams("email"), request.queryParams("password"));

            try {
                User user1 = userRepository.getByMail(user.getMail());
                return "User registred";
            } catch (UserNotFoundException e) {
                userRepository.add(user);
                return "Added Succefully";
            }

        });

        options("/user/remove/:email", (request, response) -> {
            response.header(
                    "Access-Control-Allow-Methods",
                    "DELETE, OPTIONS"
            );
            return "";
        });

        delete("/user/remove/:email", (request, response) -> {
            String email;
            email = request.params(":email");
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

        get("/user/right/", (request, response) -> {
            User user = userRepository.getByMail(request.queryParams("user"));
            return user.getRole();

        }, transformer);

        post("/user/right/update/", (request, response) -> {
            User user = userRepository.getByMail(request.queryParams("email"));
            user.setAdmin();
            return "Updates Succefully";

        });

        get("/user/getByMail/", (request, response) -> {
                return userRepository.getByMail(request.queryParams("email"));
        }, transformer);

        get("/user/getAll/", (request, response) -> {
            return userRepository.getAll();
        }, transformer);



        get("/disconnect/", (request, response) -> {
            request.session().removeAttribute("user");
            connexion = false;
            return "";
        }, transformer);

    }

}
