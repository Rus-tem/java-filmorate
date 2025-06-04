package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserDbStorage userDbStorage;
    private final FeedDbStorage feedDbStorage;

    @Autowired
    public UserService(UserDbStorage userDbStorage, FeedDbStorage feedDbStorage) {
        this.userDbStorage = userDbStorage;
        this.feedDbStorage = feedDbStorage;
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
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
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
        addFeed(id,"FRIEND", "ADD", friendId); // добавление в ленту событий
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
        addFeed(id,"FRIEND", "REMOVE", friendId); // добавление в ленту событий

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

    // Получение ленты событий пользователя
    public Collection<Feed> getFeed (Long userId) {
//        if (userId == null || userId <= 0) {
//            throw new ValidationException("Некорректное id пользователя");
//        }
//        if (userDbStorage.findById(userId).isEmpty()) {
//            throw new NotFoundException("Пользователь не найден");
//        }
        //Optional<Feed> optionalFeed = userDbStorage.getFeed(userId);
       // List<Feed> feed = optionalFeed.get();

        return feedDbStorage.getFeed(userId);
    }

    //Добавление в ленту событий
    protected Feed addFeed (Long userId, String eventType, String operation, Long entityId) {
        Feed feed = new Feed();
        Timestamp currentTimestamp = new Timestamp(new Date().getTime());
        feed.setTimestamp(currentTimestamp);
        feed.setUserId(userId);
        feed.setEventType(eventType);
        feed.setOperation(operation);
        feed.setEntityId(entityId);
        return feedDbStorage.createFeed(feed);
    }


}

