package com.grocery.quickbasket.exceptions;

public class StoreNotFoundException extends RuntimeException {
    public StoreNotFoundException(String msg) {
        super(msg);
    }
}
