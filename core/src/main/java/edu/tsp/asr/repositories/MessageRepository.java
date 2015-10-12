package edu.tsp.asr.repositories;

import edu.tsp.asr.entities.Message;
import edu.tsp.asr.entities.User;

import java.util.List;

public interface MessageRepository {
    List<Message> getMessages();
    List<Message> getMessagesByUser(User user);
}
