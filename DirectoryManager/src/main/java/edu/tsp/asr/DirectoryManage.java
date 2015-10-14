package edu.tsp.asr;

import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.UserDuplicateException;
import edu.tsp.asr.repositories.UserRepository;

import static spark.Spark.delete;
import static spark.Spark.post;

/**
 * Created by atitalla on 12/10/15.
 */
public class DirectoryManage {
    public static void main(String[] a) {
        UserRepository userRepository = new UserMemoryRepository();
        post("/addUser/", (request, response) -> {
            User user = new User(request.queryParams("email"),request.queryParams("password"));
            userRepository.addUser(user);
            return "Added Succefully";
        });

        delete("/removeUser/", (request, response) -> {
            User user = new User(request.queryParams("email"), request.queryParams("password"));
            userRepository.removeUser(user);
            return "Removed Succefully";
        });

    }
}
