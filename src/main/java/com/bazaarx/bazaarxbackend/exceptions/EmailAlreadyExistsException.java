package com.bazaarx.bazaarxbackend.exceptions;


public class EmailAlreadyExistsException extends RuntimeException {


    public EmailAlreadyExistsException(String email) {

        super("Email address already exists: " + email);
    }

    public EmailAlreadyExistsException(String email, Throwable cause) {
        super("Email address already exists: " + email, cause);
    }
}