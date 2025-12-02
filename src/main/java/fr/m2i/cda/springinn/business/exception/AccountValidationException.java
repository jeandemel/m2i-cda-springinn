package fr.m2i.cda.springinn.business.exception;

public class AccountValidationException extends BusinessException {

    public AccountValidationException() {
        super("Error activating account");
    }

}
