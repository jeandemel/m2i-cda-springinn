package fr.m2i.cda.springinn.business.exception;

public class InvalidBookingCapacityException extends BusinessException{

    public InvalidBookingCapacityException() {
        super("The guest count of the booking exceed the rooms capacities");
        
    }

}
