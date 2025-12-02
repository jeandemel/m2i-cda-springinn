package fr.m2i.cda.springinn.business;

import fr.m2i.cda.springinn.entity.Customer;

public interface CustomerAccountBusiness {

    Customer register(Customer customer);
    void activateAccount(String id);
}
