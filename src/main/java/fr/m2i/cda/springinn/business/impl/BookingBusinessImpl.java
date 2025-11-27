package fr.m2i.cda.springinn.business.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fr.m2i.cda.springinn.business.BookingBusiness;
import fr.m2i.cda.springinn.business.exception.InvalidBookingCapacityException;
import fr.m2i.cda.springinn.business.exception.RoomUnavailableException;
import fr.m2i.cda.springinn.entity.Booking;
import fr.m2i.cda.springinn.entity.Room;
import fr.m2i.cda.springinn.repository.BookingRepository;
import fr.m2i.cda.springinn.repository.RoomRepository;

@Service
public class BookingBusinessImpl implements BookingBusiness{

    private RoomRepository roomRepo;
    private BookingRepository bookingRepo;
    
    
    public BookingBusinessImpl(RoomRepository roomRepo, BookingRepository bookingRepo) {
        this.roomRepo = roomRepo;
        this.bookingRepo = bookingRepo;
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
        bookingRepo.save(booking);
        return booking;
    }

    @Override
    public void confirmBooking(String id) {
        
        Booking booking = bookingRepo.findById(id).orElseThrow();
        booking.setConfirmed(true);
        bookingRepo.save(booking);
    }

    @Override
    public Page<Booking> getAwaitingConfirmation() {
        return bookingRepo.findNotConfirmed(Pageable.ofSize(100));
    }

    @Override
    public Page<Booking> getAll(Pageable pageable) {
        return bookingRepo.findAll(pageable);
    }

}
