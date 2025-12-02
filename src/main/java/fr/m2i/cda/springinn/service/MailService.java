package fr.m2i.cda.springinn.service;

import fr.m2i.cda.springinn.entity.Booking;
import fr.m2i.cda.springinn.entity.User;

public interface MailService {
    void sendExample();
    void sendBookingCreation(Booking booking);
    void sendBookingConfirmation(Booking booking);
    void sendBookingRefused(Booking booking);
    void sendEmailValidation(User user);
}
