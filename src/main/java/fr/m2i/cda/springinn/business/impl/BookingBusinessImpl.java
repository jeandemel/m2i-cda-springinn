package fr.m2i.cda.springinn.business.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fr.m2i.cda.springinn.business.BookingBusiness;
import fr.m2i.cda.springinn.business.exception.InvalidBookingCapacityException;
import fr.m2i.cda.springinn.business.exception.RoomUnavailableException;
import fr.m2i.cda.springinn.entity.Booking;
import fr.m2i.cda.springinn.entity.Customer;
import fr.m2i.cda.springinn.entity.Room;
import fr.m2i.cda.springinn.repository.BookingRepository;
import fr.m2i.cda.springinn.repository.RoomRepository;
import fr.m2i.cda.springinn.service.MailService;

@Service
public class BookingBusinessImpl implements BookingBusiness{

    private RoomRepository roomRepo;
    private BookingRepository bookingRepo;
    private MailService mailService;
    

    public BookingBusinessImpl(RoomRepository roomRepo, BookingRepository bookingRepo, MailService mailService) {
        this.roomRepo = roomRepo;
        this.bookingRepo = bookingRepo;
        this.mailService = mailService;
    }

    @Override
    public Booking createBooking(Booking booking) {
        Integer totalCapacity = 0;
        Double totalPrice = 0.0;
        
        for (Room room : booking.getRooms()) {
            Room persistedRoom = roomRepo.findById(room.getId()).orElseThrow();
            totalCapacity+=persistedRoom.getCapacity();
            totalPrice += persistedRoom.getPrice();
        }
        if(booking.getGuestCount() > totalCapacity) {
            throw new InvalidBookingCapacityException();
        }
        totalPrice = totalPrice*booking.getDuration();
        List<Room> availableRooms = roomRepo.findAvailables(booking.getStartDate(), booking.getDuration());
        if(!availableRooms.containsAll(booking.getRooms())) {
            throw new RoomUnavailableException();
        }
        booking.setTotal(totalPrice);
        booking.setConfirmed(false);
        //TODO : modifier par le user actuellement connecté
        Customer fakeCustomer =  new Customer();
        fakeCustomer.setId("user2");
        fakeCustomer.setEmail("customer@test.com");
        booking.setCustomer(fakeCustomer);
        bookingRepo.save(booking);
        mailService.sendBookingCreation(booking);
        return booking;
    }

    @Override
    public void confirmBooking(String id) {
        
        Booking booking = bookingRepo.findById(id).orElseThrow();
        booking.setConfirmed(true);
        bookingRepo.save(booking);
        mailService.sendBookingConfirmation(booking);

    }

    @Override
    public Page<Booking> getAwaitingConfirmation() {
        return bookingRepo.findNotConfirmed(Pageable.ofSize(100));
    }

    @Override
    public Page<Booking> getAll(Pageable pageable) {
        return bookingRepo.findAll(pageable);
    }

    @Override
    public void removeBooking(String id) {
       Booking toDelete = bookingRepo.findById(id).orElseThrow();
       bookingRepo.delete(toDelete);
       if(!toDelete.getConfirmed()) {
           
           mailService.sendBookingRefused(toDelete);
        }

    }

}
