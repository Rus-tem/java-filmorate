package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final FilmService filmService;

    public DirectorController(FilmService filmService) {
        this.filmService = filmService;
    }

    // Получение списка всех режиссеров(directors)
    @GetMapping()
    public Collection<Director> getAllDirectors() {
        return filmService.getAllDirectors();
    }

    // Получение режиссера(director) по ID
    @GetMapping("/{id}")
    public Director getDirector(@PathVariable Long id) {
        return filmService.getDirector(id);
    }

    //Создание режиссера(director)
    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        return filmService.createDirector(director);
    }

    // Обновление режиссера(director)
    @PutMapping
    public Director uptadeDirector(@Valid @RequestBody Director director) {
        return filmService.uptadeDirector(director);
    }

    //Удаление режиссера(director) по ID
    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable Long id) {
        filmService.deleteDirector(id);
    }

}




