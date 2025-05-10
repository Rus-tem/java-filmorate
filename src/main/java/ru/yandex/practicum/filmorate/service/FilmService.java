package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    // Добавление лайка +
    public Film addLike(Long id, Long userId) {
        Film filmLike = null;
        User userLike = null;

        for (Film film : filmStorage.getAllFilms()) {
            if (film.getId().equals(id)) {
                filmLike = film;
                break;
            }
        }
        if (filmLike == null) {
            throw new NotFoundException("Указан некорректный id фильма");
        }
        for (User user : userStorage.getAllUsers()) {
            if (user.getId().equals(userId)) {
                filmLike.getLikesIdUser().add(userId);
                userLike = user;
                break;
            }
        }
        if (userLike == null) {
            throw new NotFoundException("Указан некорректный id фильма");
        }
        return filmLike;
    }

    // Удаление из лайка
    public Film deleteLike(Long id, Long userId) {
        Film filmDelete = null;

        for (Film film : filmStorage.getAllFilms()) {
            if (film.getId().equals(id)) {
                if (film.getLikesIdUser().contains(userId)) {
                    film.getLikesIdUser().remove(userId);
                    filmDelete = film;
                    break;
                } else {
                    throw new NotFoundException("Пользователь: " + userId + " c ID не ставил лайк");
                }
            }
        }
        if (filmDelete == null) {
            throw new NotFoundException("Фильма с таким id нет");
        }
        return filmDelete;
    }

    // Получение списка отмеченных лайком
    public Collection<Film> getPopularFilms(Long count) {
        if (count == 0) {
            return filmStorage.getAllFilms().stream()
                    .sorted(Comparator.comparing(film -> film.getLikesIdUser().size()))
                    .limit(10)
                    .toList()
                    .reversed();
        } else {
            return filmStorage.getAllFilms().stream()
                    .sorted(Comparator.comparing(film -> film.getLikesIdUser().size()))
                    .limit(count)
                    .toList()
                    .reversed();
        }
    }
}
