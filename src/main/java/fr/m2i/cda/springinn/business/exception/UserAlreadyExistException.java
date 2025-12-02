package fr.m2i.cda.springinn.business.exception;

public class UserAlreadyExistException extends BusinessException {

    public UserAlreadyExistException() {
        super("A user already exists with this email");
        //TODO Auto-generated constructor stub
    }

}
