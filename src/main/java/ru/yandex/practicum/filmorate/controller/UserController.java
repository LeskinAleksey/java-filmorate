package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    public static final String BLANK_EMAIL = "Электронная почта не может быть пустой и должна содержать символ @";
    public static final String BLANK_LOGIN = "Логин не может быть пустым";
    public static final String WRONG_BIRTHDAY = "Дата рождения не может быть в будущем";

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (!isValid(user)) {
            return null;
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь с id " + user.getId() + " добавлен");
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (!isValid(user)) {
            return null;
        }
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с id " + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        log.info("Пользователь с id " + user.getId() + " обновлен");
        return user;
    }

    private Boolean isValid(User user) {
        if (!user.getEmail().contains("@")) {
            throw new ValidationException(BLANK_EMAIL);
        }
        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            throw new ValidationException(BLANK_LOGIN);
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException(WRONG_BIRTHDAY);
        }
        return true;
    }

    private Long getNextId() {
        Long currentMaxId = users.keySet().stream().mapToLong(Long::longValue).max().orElse(0L);
        return ++currentMaxId;
    }
}
