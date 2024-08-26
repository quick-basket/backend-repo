package com.grocery.quickbasket.exceptions;

public class EmailNotExistException extends RuntimeException{
    public EmailNotExistException(String msg){
        super(msg);
    }
}
