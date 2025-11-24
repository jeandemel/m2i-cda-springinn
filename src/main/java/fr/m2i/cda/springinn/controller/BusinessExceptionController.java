package fr.m2i.cda.springinn.controller;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import fr.m2i.cda.springinn.business.exception.RoomNumberUnavaibleException;


@RestControllerAdvice
public class BusinessExceptionController {

    @ExceptionHandler(RoomNumberUnavaibleException.class)
    public ProblemDetail roomNumberUnavailable(RoomNumberUnavaibleException exception) {
        return ProblemDetail
        .forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ProblemDetail ressourcenotFound() {
        return ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, "Ressource could not be found");
    }
}
