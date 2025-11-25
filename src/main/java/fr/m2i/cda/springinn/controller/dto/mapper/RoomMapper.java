package fr.m2i.cda.springinn.controller.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import fr.m2i.cda.springinn.controller.dto.CreateRoomDTO;
import fr.m2i.cda.springinn.controller.dto.DisplayRoomDTO;
import fr.m2i.cda.springinn.controller.dto.UpdateRoomDTO;
import fr.m2i.cda.springinn.entity.Room;

@Mapper(componentModel = "spring",
     unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RoomMapper {

    Room toEntity(CreateRoomDTO dto);
    Room toEntity(UpdateRoomDTO dto);
    DisplayRoomDTO toDisplay(Room room);
    // @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    // void apply(@MappingTarget Room entity, UpdateRoomDTO dto);
}
