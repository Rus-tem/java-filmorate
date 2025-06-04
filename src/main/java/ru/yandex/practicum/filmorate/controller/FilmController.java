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
        return filmService.addLike(userId, filmId);
    }

    // Удаление лайка
    @DeleteMapping("/{filmId}/like/{userId}")
    public Film deleteFriend(@PathVariable Long filmId, @PathVariable Long userId) {
        return filmService.deleteLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}")
    public Film deleteFilm(@PathVariable Long filmId) {
        return filmService.deleteFilm(filmId);
    }

    //Получение списка фильмов отмеченных лайком
    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "0") Long count,
                                            @RequestParam(required = false) Long genreId,
                                            @RequestParam(required = false) Long year) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/search")
    public Collection<Film> search(@RequestParam String query, @RequestParam(required = false) String by) {

        return filmService.search(query, by);
    }

    // Получение фильма по ID
    @GetMapping("/{filmId}")
    public Film getFilmWithId(@PathVariable Long filmId) {
        return filmService.getFilmWithId(filmId);
    }

    //    Получение фильма с сортировкой по количеству лайков и году
    @GetMapping("/director/{directorId}{sortBy}")
    public Collection<Film> getFilmSortByLikesOrYears(@PathVariable Long directorId, @RequestParam String sortBy) {
        return filmService.getFilmSortByLikesOrYears(directorId, sortBy);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam("userId") Long userId, @RequestParam("friendId") Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}
