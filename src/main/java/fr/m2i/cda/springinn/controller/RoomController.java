package fr.m2i.cda.springinn.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.m2i.cda.springinn.business.RoomBusiness;
import fr.m2i.cda.springinn.entity.Room;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/room")
public class RoomController {

    private RoomBusiness roomBusiness;

    public RoomController(RoomBusiness roomBusiness) {
        this.roomBusiness = roomBusiness;
    }

    @GetMapping
    public Page<Room> all(@PageableDefault(page = 1, size = 5) Pageable pageable) {
        return roomBusiness.getRoomPage(pageable);
        //Faire le mapping de entité vers dto
    }

    @PostMapping
    public Room add(@RequestBody @Valid Room room) {
        //Faire le mapping de dto vers entité
        return roomBusiness.createRoom(room);
        //Puis le mapping de entité vers dto
    }


}
