package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final FilmService filmService;

    @Autowired
    public MpaController(FilmService filmService) {
        this.filmService = filmService;
    }

    //Получение всех MPA
    @GetMapping()
    public Collection<MPA> getAllMpa() {
        return filmService.getAllMpa();
    }

    // Получение MPA по ID
    @GetMapping("/{id}")
    public MPA getMpa(@PathVariable Long id) {
        return filmService.getMpa(id);
    }
}