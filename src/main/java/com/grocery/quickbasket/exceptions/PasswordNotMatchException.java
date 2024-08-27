package com.grocery.quickbasket.exceptions;

public class PasswordNotMatchException extends RuntimeException {
    public PasswordNotMatchException(String msg) {
        super(msg);
    }
}
