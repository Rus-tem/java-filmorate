package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ErrorException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

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
        Film filmId = null;
        Long idUser = null;
        if (id == null) {
            throw new ValidationException(" Не указан id фильма");
        } else if (userId == null) {
            throw new ValidationException(" Не указан id пользователя");
        }
        for (Film film : filmStorage.getAllFilms()) {
            if (film.getId().equals(id)) {
                for (User user : userStorage.getAllUsers()) {
                    if (user.getId().equals(userId)) {
                        film.getLikesIdUser().add(userId);
                        filmId = film;
                        idUser = userId;
                        System.out.println("Пользователь поставил лайк" + id);
                        break;
                    }
                }
            }
        }
        if (filmId == null) {
            throw new NotFoundException("Указан некорректный id фильма");
        } else if (idUser == null) {
            throw new NotFoundException("Пользователь с таким ID: " + userId + " не найден");
        }
        return filmId;
    }

    // Удаление из лайка
    public Film deleteLike(Long id, Long userId) {
        Film filmId = null;

        if (id == null) {
            throw new ErrorException(" Не указан id фильма");
        } else if (userId == null) {
            throw new ErrorException(" Не указан id пользователя");
        }

        for (Film film : filmStorage.getAllFilms()) {
            if (film.getId().equals(id)) {
                if (film.getLikesIdUser().contains(userId)) {
                    film.getLikesIdUser().remove(userId);
                    filmId = film;
                    System.out.println("Лайк удален");
                    break;
                } else {
                    throw new NotFoundException("Пользователь: " + userId + " c ID не ставил лайк");
                }
            }
        }
        if (filmId == null) {
            throw new NotFoundException("Фильма с таким id нет");
        }
        return filmId;
    }

    // Получение списка отмеченных лайком
    public Collection<Film> getPopularFilms(Long count) {
        List<Film> popularFilms = new ArrayList<>(filmStorage.getAllFilms());
        popularFilms.sort(Comparator.comparing(film -> film.getLikesIdUser().size()));
        if (count == 0) {
            return popularFilms.stream().limit(10).toList().reversed();
        } else {
            return popularFilms.stream().limit(count).toList().reversed();

        }
    }
}
