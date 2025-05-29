package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FilmService {

    private final FilmDbStorage filmDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, FilmDbStorage filmDbStorage, MpaDbStorage mpaDbStorage, GenreDbStorage genreDbStorage) {

        this.filmDbStorage = filmDbStorage;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
    }

    // Получение всех из таблицы films
    public List<Film> getAllFilms() {
        return new ArrayList<>(filmDbStorage.getAllFilms());
    }

    // Создание фильма в таблице films
    public Film createFilm(Film film) {
        for (Genre genre : film.getGenres()) {
            Optional<Genre> optionalGenre = genreDbStorage.getGenre(genre.getId());
            if (optionalGenre.isEmpty()) {
                throw new NotFoundException("Genre не найден");
            }
        }
        Optional<MPA> optionalMpa = mpaDbStorage.getMpa(film.getMpa().getId());
        if (optionalMpa.isEmpty()) {
            throw new NotFoundException("MPA не найден");
        }
        return filmDbStorage.create(film);
    }

    // Обновление фильма в таблице films
    public Film updateFilm(Film updateFilm) {
        filmDbStorage.update(updateFilm);
        return updateFilm;
    }

    public Film deleteFilm(Long deleteFilm) {
        if (deleteFilm == null || deleteFilm <= 0) {
            throw new ValidationException("Некорректный ID");
        }
        if (filmDbStorage.getById(deleteFilm) == null) {
            throw new NotFoundException("Фильм не найден");
        }
        Film film = filmDbStorage.getById(deleteFilm);
        filmDbStorage.deleteFilm(deleteFilm);
        return film;
    }

    // Добавление лайка фильму
    public Film addLike(Long filmId, Long userId) {
        filmDbStorage.addLike(filmId, userId);
        return filmDbStorage.findById(filmId).get();
    }

    // Удаление из лайка фильма
    public Film deleteLike(Long filmId, Long userId) {
        filmDbStorage.deleteLike(filmId, userId);
        return filmDbStorage.findById(filmId).get();
    }

    // Получение списка отмеченных лайком (список популярных фильмов)
    public Collection<Film> getPopularFilms(Long count) {
        if (count == 0) {
            return filmDbStorage.getPopularFilms().stream().limit(10).toList();
        } else {
            return filmDbStorage.getPopularFilms().stream().limit(count).toList();
        }
    }

    // Получение фильма по ID
    public Film getFilmWithId(Long filmId) {
        Film film = filmDbStorage.getById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }
        return film;
    }

    // Получение всех жанров
    public Collection<Genre> getAllGenres() {
        return new ArrayList<>(genreDbStorage.getAllGenres());
    }

    // Получение жанра по ID
    public Genre getGenre(Long id) {
        Optional<Genre> optionalGenre = genreDbStorage.getGenre(id);
        if (optionalGenre.isEmpty()) {
            throw new NotFoundException("Жанр не найден");
        }
        return optionalGenre.get();
    }

    // Получение всех MPA
    public Collection<MPA> getAllMpa() {
        return new ArrayList<>(mpaDbStorage.getAllMpa());
    }

    // Получение MPA по ID
    public MPA getMpa(Long id) {
        Optional<MPA> optionalMpa = mpaDbStorage.getMpa(id);
        if (optionalMpa.isEmpty()) {
            throw new NotFoundException("MPA не найден");
        }
        return optionalMpa.get();
    }
}
