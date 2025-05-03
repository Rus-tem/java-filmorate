package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NullFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // Добавление в друзья
    public Collection<User> addFriend(Long id, Long friendId) {
        List<User> friendsList = new ArrayList<>();

        for (User user1 : userStorage.getAllUsers()) {
            if (user1.getId().equals(id)) {
                user1.getFriendsId().add(friendId);
                friendsList.add(user1);
                break;
            }
        }
        if (friendsList.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        for (User user2 : userStorage.getAllUsers()) {
            if (user2.getId().equals(friendId)) {
                user2.getFriendsId().add(id);
                friendsList.add(user2);
                break;
            }
        }
        if (friendsList.size() == 2) {
            return friendsList;
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    // Удаление из друзей
    public Collection<User> deleteFriend(Long id, Long friendId) {
        List<User> deleteList = new ArrayList<>();

        for (User user1 : userStorage.getAllUsers()) {
            if (user1.getFriendsId().isEmpty()) {
                user1.getFriendsId().add(0L);
            }
            if (user1.getId().equals(id)) {
                user1.getFriendsId().remove(friendId);
                deleteList.add(user1);
                if (user1.getFriendsId().isEmpty()) {
                    user1.getFriendsId().add(0L);
                }
                break;
            }
        }
        if (deleteList.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        for (User user2 : userStorage.getAllUsers()) {
            if (user2.getFriendsId().isEmpty()) {
                user2.getFriendsId().add(0L);
            }
            if (user2.getId().equals(friendId)) {
                user2.getFriendsId().remove(id);
                if (user2.getFriendsId().isEmpty()) {
                    user2.getFriendsId().add(0L);
                }
                deleteList.add(user2);
            }
        }
        if (deleteList.size() == 1) {
            throw new NotFoundException("Пользователь не найден");
        }
        return deleteList;
    }

    // Получение списка друзей конкретного пользователя
    public Collection<User> getUserFriends(Long idFriend) {
        List<Long> commonFriends = new ArrayList<>();
        List<User> friends = new ArrayList<>();
        User userFriend = null;

        for (User user1 : userStorage.getAllUsers()) {
            if (user1.getId().equals(idFriend)) {
                commonFriends = new ArrayList<>(user1.getFriendsId());
                userFriend = user1;
                break;
            }
        }
        if (userFriend == null) {//(commonFriends.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        for (User user2 : userStorage.getAllUsers()) {
            for (Long friend : commonFriends) {
                if (user2.getId().equals(friend)) {
                    friends.add(user2);
                }
            }
        }
        return friends;
    }

    // Получение списка общих друзей
    public Collection<User> commonFriends(Long id, Long otherId) {
        Set<User> commonFriends = new HashSet<>();
        User user1 = null;
        User user2 = null;

        for (User userId1 : userStorage.getAllUsers()) {
            if (userId1.getId().equals(id)) {
                user1 = userId1;
                break;
            }
        }
        if (user1 == null) {
            throw new NullFoundException("Пользователь не найден");
        }
        for (User userId2 : userStorage.getAllUsers()) {
            if (userId2.getId().equals(otherId)) {
                user2 = userId2;
                break;
            }
        }
        if (user2 == null) {
            throw new NullFoundException("Пользователь не найден");
        }
        if (user1.getFriendsId().isEmpty() && user2.getFriendsId().isEmpty()) {
            throw new ValidationException("Список друзей пуст");
        }
        Set<Long> idUser1 = new HashSet<>(user1.getFriendsId());
        Set<Long> idUser2 = new HashSet<>(user2.getFriendsId());
        Set<Long> commonId = new HashSet<>(idUser1);
        commonId.retainAll(idUser2); // список общих id
        for (User user : userStorage.getAllUsers()) {
            for (Long aLong : commonId) {
                if (user.getId().equals(aLong)) {
                    commonFriends.add(user);
                }
            }
        }
        return commonFriends;
    }
}

