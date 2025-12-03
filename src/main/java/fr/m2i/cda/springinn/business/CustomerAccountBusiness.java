package fr.m2i.cda.springinn.business;

import fr.m2i.cda.springinn.entity.Customer;

public interface CustomerAccountBusiness {

    boolean emailAvailable(String email);
    Customer register(Customer customer);
    void activateAccount(String id, String hash);
}
