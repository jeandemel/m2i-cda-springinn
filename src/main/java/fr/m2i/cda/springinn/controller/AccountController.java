package fr.m2i.cda.springinn.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.m2i.cda.springinn.business.CustomerAccountBusiness;
import fr.m2i.cda.springinn.controller.dto.DisplayBookingDTO;
import fr.m2i.cda.springinn.controller.dto.RegisterCustomerDTO;
import fr.m2i.cda.springinn.controller.dto.SimpleCustomerDTO;
import fr.m2i.cda.springinn.controller.dto.mapper.BookingMapper;
import fr.m2i.cda.springinn.controller.dto.mapper.UserMapper;
import fr.m2i.cda.springinn.entity.Customer;
import fr.m2i.cda.springinn.entity.User;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private UserMapper mapper;
    private BookingMapper bookingMapper;
    private CustomerAccountBusiness accountBusiness;


    public AccountController(UserMapper mapper, BookingMapper bookingMapper, CustomerAccountBusiness accountBusiness) {
        this.mapper = mapper;
        this.bookingMapper = bookingMapper;
        this.accountBusiness = accountBusiness;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SimpleCustomerDTO registerUser(@RequestBody @Valid RegisterCustomerDTO dto) {
        Customer created = accountBusiness.register(mapper.toEntity(dto));
        return mapper.toSimpleCustomer(created);
    }

    @PostMapping("/validate/{id}/{hash}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateAccount(@PathVariable String id, @PathVariable String hash) {
        accountBusiness.activateAccount(id,hash);
    }

    @GetMapping("/available/{email}")
    public Boolean emailAvailable(@PathVariable String email) {
        return accountBusiness.emailAvailable(email);
    }

    @GetMapping
    public SimpleCustomerDTO login(@AuthenticationPrincipal User user) {
        return mapper.toSimpleCustomer(user);
    }

    @GetMapping("/bookings")
    public List<DisplayBookingDTO> getAccountBookings(@AuthenticationPrincipal Customer customer) {
        return accountBusiness.customerBookings(customer)
        .stream().map(bookingMapper::toDisplay)
        .toList();
    }
}
