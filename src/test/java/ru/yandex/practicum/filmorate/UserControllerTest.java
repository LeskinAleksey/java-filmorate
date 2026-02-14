package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateUser() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"email\":\"test@example.com\",\"login\":\"testuser\",\"name\":\"Test User\",\"birthday\":\"1990-01-01\"}", false));
    }

    @Test
    void testCreateUserWithInvalidEmail() throws Exception {
        User user = new User();
        user.setEmail("invalid-email"); // Некорректный email
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("{\"error\":\"%s\"}".formatted(UserController.BLANK_EMAIL)));
    }

    @Test
    void testCreateUserWithBlankLogin() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("{\"error\":\"%s\"}".formatted(UserController.BLANK_LOGIN)));
    }

    @Test
    void testCreateUserWithWrongBirthday() throws Exception {
        User user = new User();
        user.setLogin("testuser");
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2050, 1, 1));

        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("{\"error\":\"%s\"}".formatted(UserController.WRONG_BIRTHDAY)));
    }

}
