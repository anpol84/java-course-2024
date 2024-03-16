package edu.java.repository.jpa;

import edu.java.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface JpaChatRepositoryInterface extends JpaRepository<Chat, Long> {
    @Modifying
    @Query("DELETE FROM Chat c WHERE c.id = :id")
    int deleteChatById(Long id);

}

