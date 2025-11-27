package fr.m2i.cda.springinn.controller.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateBookingDTO {

    @NotNull
    @FutureOrPresent
    private LocalDate startDate;
    @NotNull
    @Positive
    private Integer duration;
    @NotNull
    @NotEmpty
    private List<DisplayRoomDTO> rooms;
    @NotNull
    @Positive
    private Integer guestCount;
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
