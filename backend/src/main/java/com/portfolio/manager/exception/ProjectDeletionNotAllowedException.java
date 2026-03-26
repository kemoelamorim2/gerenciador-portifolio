package com.portfolio.manager.exception;

public class ProjectDeletionNotAllowedException extends BusinessRuleException {

    public ProjectDeletionNotAllowedException(String message) {
        super(message);
    }
}
