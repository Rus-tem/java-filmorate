package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Set<Long> friendsId = new HashSet<>();
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}
