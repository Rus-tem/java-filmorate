package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final FilmService filmService;

    @Autowired
    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    //Получение всех жанров
    @GetMapping()
    public Collection<Genre> getAllGenres() {
        return filmService.getAllGenres();
    }

    // Получение жанра по ID
    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable Long id) {
        return filmService.getGenre(id);
    }
}
