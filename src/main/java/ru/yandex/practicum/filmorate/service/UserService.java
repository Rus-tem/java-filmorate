package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserDbStorage userDbStorage;

    @Autowired
    public UserService(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    // Получение всех пользователей из таблицы users
    public List<User> getAllUsers() {
        return new ArrayList<>(userDbStorage.getAllUsers());
    }

    // Создание пользователя в таблице users
    public User createUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        Optional<User> alreadyExistUser = userDbStorage.findByEmail(user.getEmail());
        if (alreadyExistUser.isPresent()) {
            throw new DuplicatedDataException("Данный имейл уже используется");
        }
        if (user.getLogin() == null || StringUtils.containsAny(user.getLogin(), " ")) {
            throw new ValidationException("Логин пользователя содержит пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("День рождения не может быть в будущем");
        }
        return userDbStorage.create(user);
    }

    // Обновление пользователя в таблице users
    public User updateUser(User updateUser) {
        if (updateUser.getId() == null) {
            throw new NullFoundException("Id должен быть указан");
        }
        Optional<User> optionalUser = userDbStorage.findById(updateUser.getId());
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        userDbStorage.update(updateUser);
        return updateUser;
    }

    // Добавление в друзья
    public Collection<User> addFriend(Long id, Long friendId) {
        Optional<User> optionalUser1 = userDbStorage.findById(id);
        Optional<User> optionalUser2 = userDbStorage.findById(friendId);

        if (optionalUser1.isEmpty() || optionalUser2.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        List<User> listFriends = new ArrayList<>();
        listFriends.add(optionalUser1.get());
        listFriends.add(optionalUser2.get());
        userDbStorage.addFriend(id, friendId);
        return listFriends;
    }

    // Получение списка друзей конкретного пользователя
    public Collection<User> getUserFriends(Long idUser) {
        Optional<User> optionalUser1 = userDbStorage.findById(idUser);

        if (optionalUser1.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        User user1 = optionalUser1.get();
        return userDbStorage.getUserFriends(user1.getId());

    }

    public User getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Некорректное id пользователя");
        }
        if (userDbStorage.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        User user = userDbStorage.findById(id).get();
        return user;

    }

    // Получение списка общих друзей
    public Collection<User> commonFriends(Long id, Long otherId) {
        Optional<User> optionalUser1 = userDbStorage.findById(id);
        Optional<User> optionalUser2 = userDbStorage.findById(otherId);

        if (optionalUser1.isEmpty() || optionalUser2.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        User user1 = optionalUser1.get();
        User user2 = optionalUser2.get();
        return userDbStorage.commonFriends(user1.getId(), user2.getId());
    }

    // Удаление из друзей
    public Collection<User> deleteFriend(Long id, Long friendId) {
        Optional<User> optionalUser1 = userDbStorage.findById(id);
        Optional<User> optionalUser2 = userDbStorage.findById(friendId);

        if (optionalUser1.isEmpty() || optionalUser2.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        List<User> listFriends = new ArrayList<>();
        listFriends.add(optionalUser1.get());
        listFriends.add(optionalUser2.get());
        userDbStorage.deleteFriend(id, friendId);
        return listFriends;
    }

    public User deleteUser(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("Некорректное id пользователя");
        }
        if (userDbStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        User user = userDbStorage.findById(userId).get();
        userDbStorage.deleteUser(userId);
        return user;
    }

}

