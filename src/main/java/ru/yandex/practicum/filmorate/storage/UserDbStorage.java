package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class UserDbStorage extends BaseStorage implements UserStorage {

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    private static final String FIND_ALL_USERS = "SELECT * FROM users";
    private static final String FIND_BY_EMAIL_USERS = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_BY_ID_USERS = "SELECT * FROM users WHERE user_id = ?";
    private static final String CREATE_NEW_USER = "INSERT INTO users(name, login, email, birthday)" +
                                                  "VALUES (?, ?, ?, ?) ;";
    private static final String UPDATE_USER = "UPDATE users SET name = ?, login = ?, email = ?, birthday = ? WHERE user_id = ?";
    private static final String ADD_FRIEND_TO_USER = "MERGE INTO FRIENDS(user_id, friends_id)" + "VALUES (?, ?);";
    private static final String FIND_USER_FRIENDS = "SELECT * FROM users, friends WHERE users.user_id = friends.friends_id AND friends.user_id = ?";
    private static final String FIND_COMMON_FRIENDS = "SELECT * FROM users U, friends F, friends O " +
                                                      "WHERE U.user_id = F.friends_id AND U.user_id = O.friends_id AND f.user_id = ? AND O.user_id = ?";
    private static final String DELETE_FRIENDS = "DELETE FROM friends WHERE user_id = ? AND friends_id = ?";
    private static final String DELETE_USER = """
            DELETE FROM likes WHERE user_id = ?;
            DELETE FROM friends WHERE user_id = ?;
            DELETE FROM friends WHERE friends_id = ?;
            DELETE FROM users WHERE user_id = ?""";

    // Получение всех пользователей из таблицы users
    @Override
    public List<User> getAllUsers() {
        return findMany(FIND_ALL_USERS);
    }

    // Создание пользователя в таблице users
    @Override
    public User create(User user) {
        long id = insert(
                CREATE_NEW_USER,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    // Обновление пользователя в таблице users
    @Override
    public User update(User newUser) {
        update(UPDATE_USER,
                newUser.getName(),
                newUser.getLogin(),
                newUser.getEmail(),
                newUser.getBirthday(),
                newUser.getId()
        );
        return newUser;
    }

    // Получение пользователя по email
    public Optional<User> findByEmail(String email) {
        return findOne(FIND_BY_EMAIL_USERS, email);
    }

    // Получение пользователя по ID
    public Optional<User> findById(long userId) {
        return findOne(FIND_BY_ID_USERS, userId);
    }

    // Добавление в друзья в таблицу friends
    public void addFriend(long userId, long friendId) {
        jdbc.update(ADD_FRIEND_TO_USER, userId, friendId);
    }

    // Получение друзей пользователя по ID
    public List<User> getUserFriends(long idUser) {
        return jdbc.query(FIND_USER_FRIENDS, mapper, idUser);
    }

    // Получение общих друзей пользователя
    public List<User> commonFriends(long id, long otherId) {
        return jdbc.query(FIND_COMMON_FRIENDS, mapper, id, otherId);
    }

    //Удаление из друзей (из таблицы friends)
    public void deleteFriend(long userId, long friendId) {
        jdbc.update(DELETE_FRIENDS, userId, friendId);
    }

    public void deleteUser(long userId) {
        jdbc.update(DELETE_USER, userId, userId, userId);
    }
}
