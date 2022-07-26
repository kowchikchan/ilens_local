package com.pbs.tech.common.exception;

public class LicenceException extends Exception {

    public LicenceException(String message, Throwable e){super(message,e);}

    public LicenceException(Throwable e){super(e);}

    public LicenceException(String message ) {super(message);}
}
