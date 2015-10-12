package edu.tsp.asr;

import edu.tsp.asr.entities.User;
import edu.tsp.asr.exceptions.UserNotFoundException;
import edu.tsp.asr.repositories.UserRepository;

import java.util.List;

import static spark.Spark.get;
import static spark.Spark.post;

public class DirectoryManager {
   public static void main(String[] a) {
        UserRepository userRepository = new UserMemoryRepository();

       get("/addUser", (request, response) -> {
           return "hellpoo";
       });

    }
}
