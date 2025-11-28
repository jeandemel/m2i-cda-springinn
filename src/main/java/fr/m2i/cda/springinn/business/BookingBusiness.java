package fr.m2i.cda.springinn.business;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fr.m2i.cda.springinn.entity.Booking;

public interface BookingBusiness {
    /**
     * Méthode pour créer un nouveau booking, vérifier la disponibilité des chambres, le nombre de guest par rapport
     * à leur capacités et calculer le total.
     * @param booking Le booking à persister
     * @return Le booking persisté non confirmé par l'administrateur
     */
    Booking createBooking(Booking booking);
    /**
     * Méthode pour confirmer une réservation
     * @param id L'id du booking à confirmer
     */
    void confirmBooking(String id);

    /**
     * Méthode pour lister les réservations en attente de confirmation
     */
    Page<Booking> getAwaitingConfirmation();
    /**
     * Méthode pour lister toutes les réservations paginées
     */
    Page<Booking> getAll(Pageable pageable);

    /**
     * Méthode pour supprimer une réservatio
     * @param id L'id de la réservatio à supprimer
     */
    void removeBooking(String id);
}
