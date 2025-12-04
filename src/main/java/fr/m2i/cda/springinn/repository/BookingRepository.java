package fr.m2i.cda.springinn.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fr.m2i.cda.springinn.entity.Booking;
import fr.m2i.cda.springinn.entity.Customer;


@Repository
public interface BookingRepository extends JpaRepository<Booking,String>{
    @Query("FROM Booking b WHERE b.confirmed = false")
    Page<Booking> findNotConfirmed(Pageable pageable);
    List<Booking> findByCustomer(Customer customer);
}
