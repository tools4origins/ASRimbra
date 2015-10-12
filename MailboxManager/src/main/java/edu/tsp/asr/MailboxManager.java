package edu.tsp.asr;

import edu.tsp.asr.repositories.UserRepository;

import static spark.Spark.get;
public class MailboxManager {
    public static void main(String[] a) {
        UserRepository userRepository = new UserMemoryRepository();

        get("/", (request, response) -> userRepository.getAllUsers());

    }
}
