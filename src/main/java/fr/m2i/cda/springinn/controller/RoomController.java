package fr.m2i.cda.springinn.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.m2i.cda.springinn.business.RoomBusiness;
import fr.m2i.cda.springinn.controller.dto.CreateRoomDTO;
import fr.m2i.cda.springinn.controller.dto.DisplayRoomDTO;
import fr.m2i.cda.springinn.controller.dto.mapper.RoomMapper;
import fr.m2i.cda.springinn.entity.Room;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/room")
public class RoomController {

    private RoomBusiness roomBusiness;
    private RoomMapper mapper;

    public RoomController(RoomBusiness roomBusiness, RoomMapper mapper) {
        this.roomBusiness = roomBusiness;
        this.mapper = mapper;
    }



    @GetMapping
    public Page<DisplayRoomDTO> all(@PageableDefault(size = 5) Pageable pageable) {
        return roomBusiness.getRoomPage(pageable).map(item -> mapper.toDisplay(item));
        
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DisplayRoomDTO add(@RequestBody @Valid CreateRoomDTO room) {
        
        return mapper.toDisplay(
            roomBusiness.createRoom(mapper.toEntity(room))
        );
        
    }

}
