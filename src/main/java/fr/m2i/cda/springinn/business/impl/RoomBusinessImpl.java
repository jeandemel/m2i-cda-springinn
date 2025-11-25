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
        throwIfExists(room);
        roomRepo.save(room);
        return room;
    }

    private void throwIfExists(Room room) {
        if(!roomNumberAvailable(room.getNumber())) {
            throw new RoomNumberUnavaibleException("Room "+room.getNumber()+" already exists");
        }
    }

    @Override
    public Room getOneRoom(String id) {        
        return roomRepo.findById(id).orElseThrow();
    }

    @Override
    public void deleteRoom(String id) {
        
        Room room = getOneRoom(id);
        roomRepo.delete(room);
    }

    @Override
    public Room updateRoom(Room room) {
        Room existing = getOneRoom(room.getId());
        if(room.getNumber() !=null && !existing.getNumber().equals(room.getNumber())) {
            throwIfExists(room);
            existing.setNumber(room.getNumber());
        }
        if(room.getCapacity() != null) {
            existing.setCapacity(room.getCapacity());
        }
        if(room.getPrice() != null) {
            existing.setPrice(room.getPrice());
        }

        roomRepo.save(existing);
        return existing;
    }

    @Override
    public Room fullUpdate(Room room) {
        Room existing = getOneRoom(room.getId());
        if(!existing.getNumber().equals(room.getNumber())) {
            throwIfExists(room);
        }
        roomRepo.save(room);
        return room;
    }

    @Override
    public boolean roomNumberAvailable(String number) {
        return roomRepo.findByNumber(number).isEmpty();
    }


}
