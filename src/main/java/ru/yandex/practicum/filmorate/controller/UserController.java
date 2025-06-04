package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    private final FilmService filmService;


    @Autowired
    public UserController(UserStorage userStorage, UserService userService, FilmService filmService) {
        this.userStorage = userStorage;
        this.userService = userService;
        this.filmService = filmService;
    }

    //Получение списка всех пользователей +
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Добавление пользователя +
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // Обновление пользователя
    @PutMapping()
    public User updateUser(@RequestBody User updateUser) { //User newUser
        return userService.updateUser(updateUser);
    }

    // Добавление в друзья +-
    @PutMapping("/{id}/friends/{friendId}")
    public Collection<User> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.addFriend(id, friendId);
    }

    // Получение списка друзей конкретного пользователя +-
    @GetMapping("/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable Long id) {
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // Получение общих друзей разных пользователей +-
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> commonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.commonFriends(id, otherId);
    }

    // Удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public Collection<User> deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @DeleteMapping("/{userId}")
    public User deleteUser(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }


    // Получение списка рекомендованных фильмов
    @GetMapping("/{userId}/recommendations")
    public Collection<Film> getFilmRecommendations(@PathVariable Long userId) {
        return filmService.getFilmRecommendations(userId);
    }

    @GetMapping("/{id}/feed")
    public Collection <Feed> getFeed (@PathVariable Long id) {
    return userService.getFeed(id);
    }
}
