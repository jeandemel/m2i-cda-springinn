package fr.m2i.cda.springinn.controller.dto;

import jakarta.validation.constraints.Positive;

public class UpdateRoomDTO {
 
    private String number;
    @Positive
    private Double price;
    @Positive
    private Integer capacity;
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public Integer getCapacity() {
        return capacity;
    }
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
