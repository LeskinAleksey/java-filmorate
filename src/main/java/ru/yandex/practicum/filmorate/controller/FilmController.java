package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    public static final String BLANK_NAME = "Название не может быть пустым";
    public static final String LONG_DESCRIPTION = "Описание не может быть длиннее 200 символов";
    public static final String WRONG_RELEASE_DATE = "Дата релиза не может быть раньше 28.12.1895";
    public static final String WRONG_DURATION = "Продолжительность фильма не может быть отрицательной";

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм с id " + film.getId() + " добавлен");
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильм с id " + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        log.info("Фильм с id " + film.getId() + " обновлен");
        return film;
    }

    private Long getNextId() {
        Long currentMaxId = films.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
        return ++currentMaxId;
    }
}
