package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetFilms() throws Exception {
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateFilm() throws Exception {
        Film film = new Film();
        film.setName("film");
        film.setDescription("comedy");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        String filmJson = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                        "id": 1,
                        "name": "film",
                        "description": "comedy",
                        "releaseDate": "2020-01-01",
                        "duration": 120
                    }
                """, false));
    }

    @Test
    void testCreateFilmWithBlankName() throws Exception {
        Film film = new Film();
        film.setName("");
        film.setDescription("comedy");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        String filmJson = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("""
                    {
                        "error": "%s"
                    }
                """.formatted(FilmController.BLANK_NAME)));
    }

    @Test
    void testCreateFilmWithLongDescription() throws Exception {
        Film film = new Film();
        film.setName("film");
        film.setDescription("comedy".repeat(100));
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        String filmJson = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("""
                    {
                        "error": "%s"
                    }
                """.formatted(FilmController.LONG_DESCRIPTION)));
    }

    @Test
    void testCreateFilmWithWrongReleaseDate() throws Exception {
        Film film = new Film();
        film.setName("film");
        film.setDescription("comedy");
        film.setReleaseDate(LocalDate.of(1800, 12, 27));
        film.setDuration(120);

        String filmJson = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("""
                    {
                        "error": "%s"
                    }
                """.formatted(FilmController.WRONG_RELEASE_DATE)));
    }
}
