package com.pbs.tech.common.exception;

public class IlensException extends Exception {
    public IlensException(String message) {
        super(message);
    }

    public IlensException(String message, Throwable cause) {
        super(message, cause);
    }
}