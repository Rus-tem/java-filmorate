package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    public static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();

    // Получаем список всех фильмов
    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получение списка всех фильмов");
        return films.values();
    }

    // Добавление фильма
    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Успешное добавление фильма: {}", film);
        return film;
    }

    // Обновление фильма
    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Id фильма не указан: {}", newFilm);
            throw new ValidationException("Id должен быть указан");
        }
        Film oldFilm = films.get(newFilm.getId());
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
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
