package fr.m2i.cda.springinn.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import fr.m2i.cda.springinn.service.MailService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@Transactional
public class RoomApiTest {

    @Autowired
    MockMvc mvc;
    @MockitoBean
    MailService mailService;

    @Test
    void getAllShouldReturnARoomPage() throws Exception {
        mvc.perform(get("/api/room"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isNotEmpty())
        .andExpect(jsonPath("$.page.totalElements").value(8))
        .andExpect(jsonPath("$.content[0].number").value("A1"));

    }


    @ParameterizedTest
    @MethodSource("availableSource")
    void getAvailableShouldReturnAvailableRooms(String startDate, String duration, int expectedLength) throws Exception {
        mvc.perform(get("/api/room/available/%s/%s".formatted(startDate,duration)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isNotEmpty())
        .andExpect(jsonPath("$.length()").value(expectedLength));

    }

    private static Stream<Arguments> availableSource() {
        return Stream.of(
            Arguments.of("2025-01-01", "10", 7),
            Arguments.of("2024-12-25", "15", 7),
            Arguments.of("2025-01-02", "1", 7),
            Arguments.of("2025-01-01", "356", 5),
            Arguments.of("2025-01-01", "170", 6)
        );
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
   void postShouldPersistNewRoom() throws Exception {
        mvc.perform(post("/api/room").with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
                "number":"E1",
                "capacity":2,
                "price":100
            }
        """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty());
   }

   @Test
    @WithMockUser(roles = {"ADMIN"})
   void postShouldDisplayErrorOnExistingRoomNumber() throws Exception {
        mvc.perform(post("/api/room").with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
                "number":"A1",
                "capacity":2,
                "price":100
            }
        """))
        .andExpect(status().isBadRequest());
   }

   @Test
    @WithMockUser(roles = {"ADMIN"})
   void postShouldDisplayValidationErrors() throws Exception {
        mvc.perform(post("/api/room").with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
            
            }
        """))
        .andExpect(status().isBadRequest());
   }


    @Test
    void getOneShouldReturnRoom() throws Exception {
        mvc.perform(get("/api/room/room1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("room1"))
        .andExpect(jsonPath("$.number").value("A1"));

    }
    @Test
    void roomAvailableShouldReturnTrueIfAvailableNumber() throws Exception {
        mvc.perform(get("/api/room/number/A3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(true));

    }
    @Test
    void roomAvailableShouldReturnFalseIfUnavailableNumber() throws Exception {
        mvc.perform(get("/api/room/number/A1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(false));

    }

    @Test
    void getOneShouldThrow404IfNoRoom() throws Exception {
        mvc.perform(get("/api/room/room100"))
        .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteShouldThrow404IfNoRoom() throws Exception {
        mvc.perform(delete("/api/room/room100").with(csrf()))
        .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void patchShouldThrow404IfNoRoom() throws Exception {
        mvc.perform(patch("/api/room/room100").with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
                "capacity":2,
                "price":100
            }
        """))
        .andExpect(status().isNotFound());

    }



    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteShouldNotThrow() throws Exception {
        mvc.perform(delete("/api/room/room1").with(csrf()))
        .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void patchShouldNotThrow() throws Exception {
        mvc.perform(patch("/api/room/room1").with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
                "capacity":2,
                "price":100
            }
        """))
        .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void patchShouldThrowIfExistingNumber() throws Exception {
        mvc.perform(patch("/api/room/room1").with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
                "number":"B1"
            }
        """))
        .andExpect(status().isBadRequest());

    }

}
