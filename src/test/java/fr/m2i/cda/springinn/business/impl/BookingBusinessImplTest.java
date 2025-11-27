package fr.m2i.cda.springinn.business.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.m2i.cda.springinn.business.exception.InvalidBookingCapacityException;
import fr.m2i.cda.springinn.business.exception.RoomUnavailableException;
import fr.m2i.cda.springinn.entity.Booking;
import fr.m2i.cda.springinn.entity.Room;
import fr.m2i.cda.springinn.repository.BookingRepository;
import fr.m2i.cda.springinn.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
public class BookingBusinessImplTest {
    @Mock
    RoomRepository roomRepo;
    @Mock
    BookingRepository bookingRepo;
    @InjectMocks
    BookingBusinessImpl instance;

    Room forTotalAndCapacity = new Room();
    List<Room> availableRooms;
    @BeforeEach
    void setUp() {
        Room firstAvailable = new Room();
        firstAvailable.setId("test1");
        
        Room secondAvailable = new Room();
        firstAvailable.setId("test2");
        availableRooms = List.of(firstAvailable,secondAvailable);

        forTotalAndCapacity.setPrice(10.0);
        forTotalAndCapacity.setCapacity(2);

    }

    @Test
    void shouldThrowIfTooManyGuests() {
        
        when(roomRepo.findById(any())).thenReturn(Optional.of(forTotalAndCapacity));

        Booking testBooking = new Booking();
        testBooking.setGuestCount(15);
        testBooking.setDuration(2);
        testBooking.setRooms(availableRooms);
        assertThrows(InvalidBookingCapacityException.class, () -> instance.createBooking(testBooking));
    }
    
    @Test
    void shouldThrowIfRoomUnavailable() {
        
        when(roomRepo.findById(any())).thenReturn(Optional.of(forTotalAndCapacity));
        when(roomRepo.findAvailables(any(), any())).thenReturn(availableRooms);

        
        Room unavailableRoom = new Room();
        unavailableRoom.setId("not-available");
        Booking testBooking = new Booking();
        testBooking.setDuration(2);
        testBooking.setGuestCount(1);
        testBooking.setRooms(List.of(unavailableRoom));
        assertThrows(RoomUnavailableException.class, () -> instance.createBooking(testBooking));
    }

    
    @Test
    void shouldSaveAndMakeTotal() {
        
        when(roomRepo.findById(any())).thenReturn(Optional.of(forTotalAndCapacity));
        when(roomRepo.findAvailables(any(), any())).thenReturn(availableRooms);

        Booking testBooking = new Booking();
        testBooking.setGuestCount(3);
        testBooking.setRooms(availableRooms);
        testBooking.setDuration(2);

        instance.createBooking(testBooking);
        assertEquals(40.0, testBooking.getTotal());
        assertFalse(testBooking.getConfirmed());
        verify(bookingRepo, times(1)).save(testBooking);
    }

}
