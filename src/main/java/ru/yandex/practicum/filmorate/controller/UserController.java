package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;


    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    //Получение списка всех пользователей
    @GetMapping()
    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    // Добавление пользователя
    @PostMapping()
    public User create(@RequestBody User user) {
        return userStorage.create(user);
    }

    // Обновление пользователя
    @PutMapping()
    public User update(@RequestBody User newUser) {
        return userStorage.update(newUser);
    }

    // Добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public Collection<User> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.addFriend(id, friendId);
    }

    // Удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public Collection<User> deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.deleteFriend(id, friendId);
    }

    // Получение списка друзей конкретного пользователя
    @GetMapping("/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable Long id) {
        return userService.getUserFriends(id);
    }

    // Получение общих друзей разных пользователей
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> commonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.commonFriends(id, otherId);
    }
}
