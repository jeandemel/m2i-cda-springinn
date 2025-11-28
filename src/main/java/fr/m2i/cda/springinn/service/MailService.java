package fr.m2i.cda.springinn.service;

import fr.m2i.cda.springinn.entity.Booking;

public interface MailService {
    void sendExample();
    void sendBookingCreation(Booking booking);
    void sendBookingConfirmation(Booking booking);
    void sendBookingRefused(Booking booking);
}
