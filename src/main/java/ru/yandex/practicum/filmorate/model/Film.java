package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.controller.FilmController.LONG_DESCRIPTION;
import static ru.yandex.practicum.filmorate.controller.FilmController.WRONG_RELEASE_DATE;

/**
 * Film.
 */
@Data
public class Film {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    @Positive
    private int duration;

    @AssertTrue(message = WRONG_RELEASE_DATE)
    public boolean isReleaseDateValid() {
        return releaseDate == null || !releaseDate.isBefore(LocalDate.of(1895, 12, 28));
    }

    @AssertTrue(message = LONG_DESCRIPTION)
    public boolean isDescriptionValid() {
        return description == null || description.length() <= 200;
    }

}
