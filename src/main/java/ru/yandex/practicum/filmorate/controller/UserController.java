package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    public static final Logger log = LoggerFactory.getLogger(UserController.class);


    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получение списка всех пользователей");
        return users.values();
    }

    // Добавление пользователя
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (StringUtils.containsAny(user.getLogin(), " ")) {
            log.error("Логин пользователя содержит пробелы: {}", user);
            throw new ValidationException("Логин пользователя содержит пробелы");
        }
        users.put(user.getId(), user);
        log.info("Успешное добавление пользователя: {}", user);
        return user;
    }

    // Обновление пользователя
    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.error("Id пользователя не указан: {}", newUser);
            throw new ValidationException("Id должен быть указан");
        }
        User oldUser = users.get(newUser.getId());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setBirthday(newUser.getBirthday());
        if (StringUtils.containsAny(newUser.getLogin(), " ")) {
            log.error("Логин пользователя содержит пробелы: {}", newUser);
            throw new ValidationException("Логин пользователя содержит пробелы");
        } else {
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            oldUser.setName(newUser.getLogin());
        } else {
            oldUser.setName(newUser.getName());
        }
        return newUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
