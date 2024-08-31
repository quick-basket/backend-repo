package com.grocery.quickbasket.exceptions;

public class UserIdNotFoundException extends RuntimeException {
    public UserIdNotFoundException(String msg) {
        super(msg);
    }
}
