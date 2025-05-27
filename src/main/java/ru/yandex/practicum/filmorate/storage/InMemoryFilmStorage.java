package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Qualifier
public class InMemoryFilmStorage implements FilmStorage {

    public static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();

    public Collection<Film> getAllFilm() {
        log.info("Получение списка всех фильмов");
        return films.values();
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Успешное добавление фильма: {}", film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        Film oldFilm = null;
        if (newFilm.getId() == null) {
            log.error("Id фильма не указан: {}", newFilm);
            throw new ValidationException("Id должен быть указан");
        }
        for (Film film : films.values()) {
            if (film.getId().equals(newFilm.getId())) {
                oldFilm = films.get(newFilm.getId());
                oldFilm.setName(newFilm.getName());
                oldFilm.setDescription(newFilm.getDescription());
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
                oldFilm.setDuration(newFilm.getDuration());
            }
        }
        if (oldFilm == null) {
            throw new NotFoundException("Фильм с таким ID:" + newFilm.getId() + " не найден");
        }
        return oldFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
