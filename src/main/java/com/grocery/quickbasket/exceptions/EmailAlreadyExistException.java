package com.grocery.quickbasket.exceptions;

public class EmailAlreadyExistException extends RuntimeException{
    public EmailAlreadyExistException(String msg){
        super(msg);
    }
}
