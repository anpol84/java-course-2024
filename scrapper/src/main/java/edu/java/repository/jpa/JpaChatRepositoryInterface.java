package edu.java.repository.jpa;

import edu.java.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;


public interface JpaChatRepositoryInterface extends JpaRepository<Chat, Long> {

    int deleteChatById(Long id);

}

