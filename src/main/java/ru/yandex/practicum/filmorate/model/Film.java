package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.yandex.practicum.filmorate.annotation.CheckDateFilm;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    private Long id;
    @NotBlank(message = "Имя не должно быть пустым")
    private String name;
    @Size(min = 1, max = 200, message = "Описание не должно быть больше 200 символов и не менее 1")
    private String description;
    @CheckDateFilm(message = "Дата релиза не должна быть ранее {value}")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма может быть только положительное число")
    private long duration;
}
