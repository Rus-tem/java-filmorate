package ru.yandex.practicum.filmorate.storage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NullFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage { // перенесено!
    public static final Logger log = LoggerFactory.getLogger(UserController.class);
    protected final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAllUsers() {
        log.info("Получение списка всех пользователей");
        return users.values();
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (StringUtils.containsAny(user.getLogin(), " ") || user.getLogin() == null) {
            log.error("Логин пользователя содержит пробелы: {}", user);
            throw new ValidationException("Логин пользователя содержит пробелы");
        } else if (user.getEmail() == null) {
            log.error("Не указан email: {}", user);
            throw new NullFoundException("Не указан email");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("День рождения не может быть в будущем: {}", user);
            throw new ValidationException("День рождения не может быть в будущем");
        }
        users.put(user.getId(), user);
        log.info("Успешное добавление пользователя: {}", user);
        return user;
    }

    @Override
    public User update(User newUser) {
        User oldUser = null;
        if (newUser.getId() == null) {
            log.error("Id пользователя не указан: {}", newUser);
            throw new NullFoundException("Id должен быть указан");
        }
        for (User user : users.values()) {
            if (user.getId().equals(newUser.getId())) {
                oldUser = users.get(newUser.getId());
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
                break;
            }
        }
        if (oldUser == null) {
            log.error("Пользователя с таким ID нет: {}", newUser);
            throw new NotFoundException("Пользователя с таким ID нет");
        }
        return oldUser;
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
