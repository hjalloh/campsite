package com.upgrade.interview.campsite.exception;

public class CampsiteAlreadyBookedException extends RuntimeException {

    public CampsiteAlreadyBookedException(String message) {
        super(message);
    }

}
