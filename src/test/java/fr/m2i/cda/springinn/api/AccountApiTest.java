package fr.m2i.cda.springinn.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import fr.m2i.cda.springinn.entity.User;
import fr.m2i.cda.springinn.repository.UserRepository;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@Transactional
public class AccountApiTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    UserRepository userRepo;


    @Test
    void postShouldPersistNewCustomer() throws Exception {
        mvc.perform(post("/api/account").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "email":"test@test.com",
                                "password":"1234",
                                "firstName":"test",
                                "name":"toust",
                                "phoneNumber":"000000000000",
                                "address":"address"
                            }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());

        User user = userRepo.findByEmail("test@test.com").orElseThrow();
        assertNotEquals("1234", user.getPassword());
        assertFalse(user.getActive());
    }


    @Test
    void postShouldNotPersistOnEmailTaken() throws Exception {
        mvc.perform(post("/api/account").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "email":"customer@test.com",
                                "password":"1234",
                                "firstName":"test",
                                "name":"toust",
                                "phoneNumber":"000000000000",
                                "address":"address"
                            }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldActivateUser() throws Exception {
        mvc.perform(post("/api/account/validate/user3/JDJhJDEwJFNXeGtjdWFGdjN6UGM5RjdJVjUvcC5rNmlUOS9oV2oxRy5ZbGQxVjZ5V1VSLkJEeVZITnF1").with(csrf()))
        .andExpect(status().isNoContent());

        

        User user = userRepo.findById("user3").orElseThrow();
        
        assertTrue(user.getActive());
    }

    @Test
    @WithUserDetails("customer@test.com")
    void shouldReturnUserAccount() throws Exception {
        mvc.perform(get("/api/account"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("customer@test.com"))
        .andExpect(jsonPath("$.role").value("ROLE_CUSTOMER"))
        .andExpect(jsonPath("$.id").value("user2"));
        
        
    }

    @Test
    void emailAvailableShouldReturnTrueIfAvailableEmail() throws Exception {
        mvc.perform(get("/api/account/available/test@test.com"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(true));
        

    }
    @Test
    void emailAvailableShouldReturnFalseIfUnAvailableEmail() throws Exception {
        mvc.perform(get("/api/account/available/customer@test.com"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(false));

    }

    @Test
    @WithUserDetails("customer@test.com")
    void shouldReturnCustomerBookings() throws Exception {
        mvc.perform(get("/api/account/bookings"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value("booking1"))
        .andExpect(jsonPath("$[1].id").value("booking2"));
        
        
    }
}
