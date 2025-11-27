package fr.m2i.cda.springinn.business.exception;

public class RoomUnavailableException extends BusinessException {

    public RoomUnavailableException() {
        super("A room you are trying to book is not available");
        
    }

}
