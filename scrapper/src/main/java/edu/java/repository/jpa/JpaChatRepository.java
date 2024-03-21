package edu.java.repository.jpa;

import edu.java.model.Chat;
import edu.java.repository.ChatRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class JpaChatRepository implements ChatRepository {
    private final JpaChatRepositoryInterface jpaChatRepositoryInterface;

    @Override
    public void add(Chat chat) {
        jpaChatRepositoryInterface.save(chat);
    }

    @Override
    public int remove(Long id) {
        return jpaChatRepositoryInterface.deleteChatById(id);
    }

    @Override
    public Optional<Chat> findById(Long id) {
        return jpaChatRepositoryInterface.findById(id);
    }

    @Override
    public List<Chat> findAll() {
        return jpaChatRepositoryInterface.findAll();
    }
}

