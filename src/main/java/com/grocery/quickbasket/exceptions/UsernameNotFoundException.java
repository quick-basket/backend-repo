package com.grocery.quickbasket.exceptions;

import javax.naming.AuthenticationException;

public class UsernameNotFoundException extends AuthenticationException {
    public UsernameNotFoundException(String msg){
        super(msg);
    }
}
