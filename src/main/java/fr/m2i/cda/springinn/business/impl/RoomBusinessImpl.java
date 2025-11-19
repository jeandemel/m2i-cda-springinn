package fr.m2i.cda.springinn.business.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fr.m2i.cda.springinn.business.RoomBusiness;
import fr.m2i.cda.springinn.business.exception.RoomNumberUnavaibleException;
import fr.m2i.cda.springinn.entity.Room;
import fr.m2i.cda.springinn.repository.RoomRepository;

@Service
public class RoomBusinessImpl implements RoomBusiness{

    private RoomRepository roomRepo;

    public RoomBusinessImpl(RoomRepository roomRepo) {
        this.roomRepo = roomRepo;
    }

    @Override
    public Page<Room> getRoomPage(Pageable pageable) {
        return roomRepo.findAll(pageable);
    }

    @Override
    public Room createRoom(Room room) {
        if(roomRepo.findByNumber(room.getNumber()).isPresent()) {
            throw new RoomNumberUnavaibleException("Room "+room.getNumber()+" already exists");
        }
        roomRepo.save(room);
        return room;
    }


}
