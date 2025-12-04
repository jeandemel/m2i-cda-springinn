package fr.m2i.cda.springinn.business;

import java.util.List;

import fr.m2i.cda.springinn.entity.Booking;
import fr.m2i.cda.springinn.entity.Customer;

public interface CustomerAccountBusiness {

    boolean emailAvailable(String email);
    Customer register(Customer customer);
    void activateAccount(String id, String hash);
    List<Booking> customerBookings(Customer customer);
}
