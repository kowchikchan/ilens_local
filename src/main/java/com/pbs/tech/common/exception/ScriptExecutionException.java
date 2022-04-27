package com.pbs.tech.common.exception;

public class ScriptExecutionException extends RuntimeException {
    public ScriptExecutionException(String message) {
        super(message);
    }

    public ScriptExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}