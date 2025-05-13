package com.assignment.DocIngest.exceptions;


public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
        System.out.println("UserNotFoundException::"+message);

    }
}