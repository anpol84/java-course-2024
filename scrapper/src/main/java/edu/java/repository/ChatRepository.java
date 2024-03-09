package edu.java.repository;

import edu.java.model.Chat;
import java.util.List;
import java.util.Optional;


public interface ChatRepository {

    void add(Long id);

    int remove(Long id);

    Optional<Chat> findById(Long id);

    List<Chat> findAll();
}
