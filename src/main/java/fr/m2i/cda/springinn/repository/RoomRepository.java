package fr.m2i.cda.springinn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.m2i.cda.springinn.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room,String>{
    Optional<Room> findByNumber(String number);
}
