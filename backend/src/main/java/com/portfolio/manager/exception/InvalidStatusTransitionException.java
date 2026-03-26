package com.portfolio.manager.exception;

public class InvalidStatusTransitionException extends BusinessRuleException {

    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}
