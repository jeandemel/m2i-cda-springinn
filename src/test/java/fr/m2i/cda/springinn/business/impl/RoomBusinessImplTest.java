package fr.m2i.cda.springinn.business.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import fr.m2i.cda.springinn.business.exception.RoomNumberUnavaibleException;
import fr.m2i.cda.springinn.entity.Room;
import fr.m2i.cda.springinn.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
public class RoomBusinessImplTest {
    @Mock
    RoomRepository repo;
    @InjectMocks
    RoomBusinessImpl instance;

    @Test
    void getRoomPageShouldCallRepository() {
        Pageable pageable = PageRequest.of(0, 5);
        instance.getRoomPage(pageable);
        verify(repo, times(1)).findAll(pageable);

    }

    @Test
    void createRoomShouldCallSaveOnAvailableNumber() {
        Room toCreate = new Room();
        toCreate.setNumber("test");
        // On dit au mock de renvoyer un optional vide pour ne pas passer dans le if qui
        // throw
        when(repo.findByNumber("test")).thenReturn(Optional.empty());
        // On appel la méthode
        instance.createRoom(toCreate);
        // On s'attend à ce que le save du repo ait été appelé
        verify(repo, times(1)).save(toCreate);
    }

    @Test
    void createRoomShouldThrowOnNumberUnavailable() {
        Room toCreate = new Room();
        toCreate.setNumber("test");
        // On dit au mock de renvoyer un optional plein pour provoquer le throw
        when(repo.findByNumber("test")).thenReturn(Optional.of(new Room()));

        assertThrows(RoomNumberUnavaibleException.class, () -> instance.createRoom(toCreate));
    }

    @Test
    void updateRoomShouldCallSaveOnAvailableNumber() {
        Room toUpdate = new Room();
        toUpdate.setNumber("test");
        // On dit au mock de renvoyer un optional vide pour ne pas passer dans le if qui
        // throw
        when(repo.findByNumber("test")).thenReturn(Optional.empty());
        // On appel la méthode
        instance.updateRoom(toUpdate);
        // On s'attend à ce que le save du repo ait été appelé
        verify(repo, times(1)).save(toUpdate);
    }

    @Test
    void updateRoomShouldThrowOnNumberUnavailable() {
        Room toUpdate = new Room();
        toUpdate.setNumber("test");
        // On dit au mock de renvoyer un optional plein pour provoquer le throw
        when(repo.findByNumber("test")).thenReturn(Optional.of(new Room()));

        assertThrows(RoomNumberUnavaibleException.class, () -> instance.updateRoom(toUpdate));
    }

    @Test
    void getOneShouldReturnARoom() {
        Room expected = new Room();
        when(repo.findById("test")).thenReturn(Optional.of(expected));

        assertEquals(expected, instance.getOneRoom("test"));
        verify(repo, times(1)).findById("test");

    }

    @Test
    void getOneShouldThrowIfNoRoom() {

        when(repo.findById("test")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> instance.getOneRoom("test"));

    }



    @Test
    void deleteShouldCallDelete() {
        Room toDelete = new Room();
        when(repo.findById("test")).thenReturn(Optional.of(toDelete));
        instance.deleteRoom("test");
        verify(repo, times(1)).delete(toDelete);
    }
}
