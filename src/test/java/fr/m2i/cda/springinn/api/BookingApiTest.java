package fr.m2i.cda.springinn.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import fr.m2i.cda.springinn.repository.BookingRepository;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@Transactional
public class BookingApiTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    BookingRepository bookingRepo;

    @Test
    @WithUserDetails("customer@test.com")
    void postShouldPersistNewBooking() throws Exception {
        mvc.perform(post("/api/booking").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "startDate":"2026-01-01",
                                "duration":2,
                                "guestCount":2,
                                "rooms":[
                                    {"id":"room1"},
                                    {"id":"room2"}
                                ]
                            }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.total").value(300))
                .andExpect(jsonPath("$.confirmed").value(false));
    }

    @Test
    @WithUserDetails("customer@test.com")
    void postShouldHaveErrorOnTooManyGuest() throws Exception {
        mvc.perform(post("/api/booking").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "startDate":"2026-01-01",
                                "duration":2,
                                "guestCount":20,
                                "rooms":[
                                    {"id":"room1"},
                                    {"id":"room2"}
                                ]
                            }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("customer@test.com")
    void postShouldHaveErrorOnUnavailableRoom() throws Exception {
        mvc.perform(post("/api/booking").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "startDate":"2025-01-01",
                                "duration":2,
                                "guestCount":2,
                                "rooms":[
                                    {"id":"room1"},
                                    {"id":"room2"}
                                ]
                            }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void patchConfirmShouldPassConfirmedToTrue() throws Exception {
        mvc.perform(patch("/api/booking/confirm/booking3").with(csrf()))
                .andExpect(status().isNoContent());

        assertTrue(bookingRepo.findById("booking3").get().getConfirmed());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getShouldReturnAllBookings() throws Exception {
        mvc.perform(get("/api/booking"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.totalElements").value(3))
            .andExpect(jsonPath("$.content[0].id").value("booking1"))
            ;

    }
    
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getShouldReturnAllBookingsAwaitingConfirmation() throws Exception {
        mvc.perform(get("/api/booking?awaitingConfirm=true"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.totalElements").value(1))
            .andExpect(jsonPath("$.content[0].id").value("booking3"))
            .andExpect(jsonPath("$.content[0].confirmed").value(false))
            ;

    }

     @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteShouldRemoveExistingBooking() throws Exception {
        mvc.perform(delete("/api/booking/booking3").with(csrf()))
                .andExpect(status().isNoContent());

        assertTrue(bookingRepo.findById("booking3").isEmpty());
    }
}
