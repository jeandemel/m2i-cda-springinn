package fr.m2i.cda.springinn.controller.dto;

public class AdminDisplayBookingDTO extends DisplayBookingDTO{
    private SimpleCustomerDTO customer;
    
    public SimpleCustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(SimpleCustomerDTO customer) {
        this.customer = customer;
    }


}
