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

    //Ici on utilise une nativeQuery, ce qui signifie que cette requête est fortement lié au SGBD utilisé et 
    //pourrait ne pas marcher par exemple dans les tests avec H2 ou si la structure de la bdd change
    @Query( value = """
        SELECT * FROM room WHERE room.id NOT IN 
        (SELECT br.rooms_id FROM booking b  
        INNER JOIN booking_rooms br ON b.id=br.bookings_id 
        WHERE b.start_date < :startDate + INTERVAL :duration AND b.start_date + INTERVAL b.duration DAY > :startDate)
        """, nativeQuery = true)
    List<Room> findAvailables(LocalDate startDate, Integer duration);
}
