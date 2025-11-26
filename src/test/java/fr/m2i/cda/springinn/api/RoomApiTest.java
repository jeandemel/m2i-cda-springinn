package fr.m2i.cda.springinn.api;

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
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@Transactional
public class RoomApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void getAllShouldReturnARoomPage() throws Exception {
        mvc.perform(get("/api/room"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isNotEmpty())
        .andExpect(jsonPath("$.page.totalElements").value(8))
        .andExpect(jsonPath("$.content[0].number").value("A1"));

    }


    @Test
   void postShouldPersistNewRoom() throws Exception {
        mvc.perform(post("/api/room")
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
   void postShouldDisplayErrorOnExistingRoomNumber() throws Exception {
        mvc.perform(post("/api/room")
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
   void postShouldDisplayValidationErrors() throws Exception {
        mvc.perform(post("/api/room")
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
    void deleteShouldThrow404IfNoRoom() throws Exception {
        mvc.perform(delete("/api/room/room100"))
        .andExpect(status().isNotFound());

    }

    @Test
    void patchShouldThrow404IfNoRoom() throws Exception {
        mvc.perform(patch("/api/room/room100")
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
    void deleteShouldNotThrow() throws Exception {
        mvc.perform(delete("/api/room/room1"))
        .andExpect(status().isNoContent());

    }

    @Test
    void patchShouldNotThrow() throws Exception {
        mvc.perform(patch("/api/room/room1")
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
    void patchShouldThrowIfExistingNumber() throws Exception {
        mvc.perform(patch("/api/room/room1")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
                "number":"B1"
            }
        """))
        .andExpect(status().isBadRequest());

    }

}
