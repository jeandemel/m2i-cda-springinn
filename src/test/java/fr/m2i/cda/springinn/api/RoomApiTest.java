package fr.m2i.cda.springinn.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        .andExpect(jsonPath("$.page.totalElements").value(4))
        .andExpect(jsonPath("$.content[0].number").value("A1"));

    }


    @Test
   void postShouldPersistNewRoom() throws Exception {
        mvc.perform(post("/api/room")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
                "number":"C1",
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

}
