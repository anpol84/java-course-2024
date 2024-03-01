package edu.java.service;


public interface ChatService {
    void register(Long tgChatId);

    void unregister(Long tgChatId);
}
