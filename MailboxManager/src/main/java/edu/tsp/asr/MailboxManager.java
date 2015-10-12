package edu.tsp.asr;

import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.UserNotFoundException;
import edu.tsp.asr.repositories.UserRepository;

import static spark.Spark.get;
import static spark.Spark.post;

public class MailboxManager {
    public static void main(String[] a) {
        UserRepository userRepository = new UserMemoryRepository();

        get("/", (request, response) -> userRepository.getAllUsers());

        post("/connect/", (request, response) -> {
            try {
                User user = userRepository.getUserByCredentials(request.queryParams("mail"), request.queryParams("password"));
                request.session().attribute("user", user);
                return "";
            } catch (UserNotFoundException e) {
                return "Bad credentials :(";
            }
        });

        get("/disconnect/", (request, response) -> {
            request.session().removeAttribute("user");
            return "";
        });

        get("/mailbox/", ((request, response) -> {
            User user = request.session().attribute("user");
            if (user == null) {
                response.status(403);
                return "You not logged in.";
            }

            return "Welcome "+user.getMail();
        }));



    }
}
