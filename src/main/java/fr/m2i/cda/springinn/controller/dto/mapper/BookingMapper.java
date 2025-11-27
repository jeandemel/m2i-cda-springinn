package fr.m2i.cda.springinn.controller.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import fr.m2i.cda.springinn.controller.dto.CreateBookingDTO;
import fr.m2i.cda.springinn.controller.dto.DisplayBookingDTO;
import fr.m2i.cda.springinn.entity.Booking;

@Mapper(componentModel = "spring",
     unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BookingMapper {

    Booking toEntity(CreateBookingDTO dto);
    DisplayBookingDTO toDisplay(Booking entity);
}
