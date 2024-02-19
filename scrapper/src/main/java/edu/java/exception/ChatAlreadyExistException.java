package edu.java.exception;


public class ChatAlreadyExistException extends RuntimeException {
    public ChatAlreadyExistException(String message) {
        super(message);
    }
}
