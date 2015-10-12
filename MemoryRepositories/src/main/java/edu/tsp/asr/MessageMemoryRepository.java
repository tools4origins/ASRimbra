package edu.tsp.asr;

import edu.tsp.asr.entities.Message;
import edu.tsp.asr.entities.User;
import edu.tsp.asr.repositories.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MessageMemoryRepository implements MessageRepository {
    private ArrayList<Message> messages = new ArrayList<>();

    @Override
    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public List<Message> getMessagesByUser(User user) {
        return messages.stream()
                .filter(m->m.getUser().equals(user))
                .collect(Collectors.toList());
    }
}
