package com.pbs.tech.common.exception;

public class AuthenticationException  extends Exception {
    public AuthenticationException(String message, Throwable e){
        super(message,e);

    }

    public AuthenticationException(Throwable e){
        super(e);
    }

    public AuthenticationException(String message ) {
        super(message);

    }
}
