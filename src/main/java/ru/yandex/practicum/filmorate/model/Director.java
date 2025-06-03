package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Director {
    private long id;
    private String name;

    public Director(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Director() {
    }
}
