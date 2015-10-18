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
    public static boolean connexion=false;

    public static void main(String[] a) {
        UserRepository userRepository = new UserMemoryRepository();

        try {
            userRepository.addUser(new User("guyomarc@tem-tsp.eu", "passwd"));
            userRepository.addUser(new User("atilalla@tem-tsp.eu", "passwd2"));
        } catch (StorageException e) {
            e.printStackTrace();
        }
        try {
            User u = userRepository.getUserByMail("guyomarc@tem-tsp.eu");
            u.setAdmin();
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        } catch (StorageException e) {
            e.printStackTrace();
        }
        ResponseTransformer transformer = new JsonTransformer();
        port(7654);
        get("/", (rq, rs) -> {
            if (connexion)
               return userRepository.getAllUsers();
            else return "You have to connect";

        },transformer);


        post("/user/addUser/", (request, response) -> {
            if (connexion) {
                User user = new User(request.queryParams("email"), request.queryParams("password"));

                try {
                    User user1 = userRepository.getUserByMail(user.getMail());
                    return "User registred";
                } catch (UserNotFoundException e) {
                    userRepository.addUser(user);
                    return "Added Succefully";
                }
            }
            else return "You have to connect";
        });

        delete("/user/removeUser/", (request, response) -> {
            if( connexion) {
                User user = userRepository.getUserByMail(request.queryParams("email"));
                System.out.println(user.getMail());
                userRepository.removeUser(user);
                return "Removed Succefully";
            }
            else return "You have to connect";

        });

        get("/user/right/", (request, response) -> {
            if (connexion){
            User user = userRepository.getUserByMail(request.queryParams("user"));
            return user.getRole();
            }
            else return "You have to connect";
        },transformer);

        post("/user/right/update/", (request, response) -> {
            if (connexion) {
                User user = userRepository.getUserByMail(request.queryParams("email"));
                user.setAdmin();
                return "Updates Succefully";
            }
            else return "You have to connect";
        });

        get("/user/getUserByMail/", (request, response) -> {
            if (connexion){
            User user = userRepository.getUserByMail(request.queryParams("email"));
            return user;
            }
            else return "You have to connect";
        },transformer);

        get("/user/getAllUsers/", (request, response) -> {
            if (connexion){
            return userRepository.getAllUsers();
            }
            else return "You have to connect";
        },transformer);

        post("/connect/", (request, response) -> {
            try {
                User user = userRepository.getUserByCredentials(
                        request.queryParams("mail"),
                        request.queryParams("password")
                );
                if (user.getRole() == Role.ADMIN) {
                    request.session().attribute("user", user);
                    connexion=true;
                    response.redirect("/user/getAllUsers/");
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

        get("/disconnect/", (request, response) -> {
            request.session().removeAttribute("user");
            connexion=false;
            return "";
        }, transformer);

    }

}
