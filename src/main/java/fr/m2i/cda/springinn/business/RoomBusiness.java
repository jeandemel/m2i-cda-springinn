package fr.m2i.cda.springinn.business;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fr.m2i.cda.springinn.entity.Room;

public interface RoomBusiness {
    Page<Room> getRoomPage(Pageable pageable);
    Room createRoom(Room room);
    Room getOneRoom(String id);
    void deleteRoom(String id);
    Room updateRoom(Room room);
    //Avoir les deux update c'est pas génial, mais c'est juste pour avoir les deux possibilités via put ou patch
    Room fullUpdate(Room room);
    boolean roomNumberAvailable(String number);
}
