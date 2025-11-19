package fr.m2i.cda.springinn.business;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fr.m2i.cda.springinn.entity.Room;

public interface RoomBusiness {
    Page<Room> getRoomPage(Pageable pageable);
    Room createRoom(Room room);
}
