package fr.m2i.cda.springinn.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.m2i.cda.springinn.business.BookingBusiness;
import fr.m2i.cda.springinn.controller.dto.CreateBookingDTO;
import fr.m2i.cda.springinn.controller.dto.DisplayBookingDTO;
import fr.m2i.cda.springinn.controller.dto.mapper.BookingMapper;
import fr.m2i.cda.springinn.entity.Booking;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    private BookingBusiness bookingBusiness;
    private BookingMapper bookingMapper;
    public BookingController(BookingBusiness bookingBusiness, BookingMapper bookingMapper) {
        this.bookingBusiness = bookingBusiness;
        this.bookingMapper = bookingMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DisplayBookingDTO addBooking(@RequestBody @Valid CreateBookingDTO dto) {
        Booking toAdd = bookingMapper.toEntity(dto);
        return bookingMapper.toDisplay(bookingBusiness.createBooking(toAdd));
    }

    @PatchMapping("/confirm/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirm(@PathVariable String id) {
        bookingBusiness.confirmBooking(id);
    }
}
