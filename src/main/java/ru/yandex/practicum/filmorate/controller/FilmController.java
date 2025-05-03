package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    // Получаем список всех фильмов
    @GetMapping()
    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    // Добавление фильма
    @PostMapping()
    public Film create(@Valid @RequestBody Film film) {
        return filmStorage.create(film);
    }

    // Обновление фильма
    @PutMapping()
    public Film update(@Valid @RequestBody Film newFilm) {
        return filmStorage.update(newFilm);
    }

    //Добавление лайка
    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.addLike(id, userId);
    }

    // Удаление лайка
    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteFriend(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.deleteLike(id, userId);
    }

    //Получение списка фильмов отмеченных лайком
    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "0") Long count) {
        return filmService.getPopularFilms(count);
    }

}
