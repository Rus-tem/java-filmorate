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
        return filmService.getAllFilms();
    }

    // Добавление фильма
    @PostMapping()
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    // Обновление фильма
    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        return filmService.updateFilm(newFilm);
    }

    //Добавление лайка
    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        return filmService.addLike(filmId, userId);
    }

    // Удаление лайка
    @DeleteMapping("/{filmId}/like/{userId}")
    public Film deleteLike(@PathVariable Long filmId, @PathVariable Long userId) {
        return filmService.deleteLike(filmId, userId);
    }

    //Получение списка фильмов отмеченных лайком
    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "0") Long count) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/{filmId}")
    public Film getFilmWithId(@PathVariable Long filmId) {
        return filmService.getFilmWithId(filmId);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam("userId") Long userId, @RequestParam("friendId") Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}
