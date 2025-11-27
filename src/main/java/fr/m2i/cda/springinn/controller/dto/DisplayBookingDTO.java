package fr.m2i.cda.springinn.controller.dto;

import java.time.LocalDate;
import java.util.List;

public class DisplayBookingDTO {

    private String id;
    private LocalDate startDate;
    private Integer duration;
    private List<DisplayRoomDTO> rooms;
    private Integer guestCount;
    private Double total;
    private Boolean confirmed;
    public Boolean getConfirmed() {
        return confirmed;
    }
    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }
    public Double getTotal() {
        return total;
    }
    public void setTotal(Double total) {
        this.total = total;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public Integer getDuration() {
        return duration;
    }
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    public List<DisplayRoomDTO> getRooms() {
        return rooms;
    }
    public void setRooms(List<DisplayRoomDTO> rooms) {
        this.rooms = rooms;
    }
    public Integer getGuestCount() {
        return guestCount;
    }
    public void setGuestCount(Integer guestCount) {
        this.guestCount = guestCount;
    }
}
