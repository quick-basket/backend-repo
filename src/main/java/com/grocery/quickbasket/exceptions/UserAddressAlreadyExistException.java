package com.grocery.quickbasket.exceptions;

public class UserAddressAlreadyExistException extends RuntimeException {
    public UserAddressAlreadyExistException() {
        super("User address already exists");
    }
}
