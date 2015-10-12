package edu.tsp.asr;

import edu.tsp.asr.repositories.UserRepository;

import static spark.Spark.get;
public class DirectoryManager {
    public void add(User user);
    public void remove(User user);
    public void update(User user);
    public User lookup(String name);

    public static void main(String[] a) {


    }
}
