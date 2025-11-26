package fr.m2i.cda.springinn.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fr.m2i.cda.springinn.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room,String>{
    Optional<Room> findByNumber(String number);


    
    @Query("""
        FROM Room r WHERE r NOT IN (SELECT b.rooms FROM Booking b
        WHERE b.startDate < (:startDate + :duration DAY) AND (b.startDate + b.duration DAY) > :startDate)
        """)
    List<Room> findAvailables(LocalDate startDate, Integer duration);
}
