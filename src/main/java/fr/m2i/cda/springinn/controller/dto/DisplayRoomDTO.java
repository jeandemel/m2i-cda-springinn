package fr.m2i.cda.springinn.controller.dto;

public class DisplayRoomDTO {

    private String id;
    private String number;
    private Double price;
    private Integer capacity;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
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
