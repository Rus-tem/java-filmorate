package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.CheckDateUserBirthday;

import java.time.LocalDate;

@Data
public class User {
    private Long id;
    @Email(message = "Не корректные email")
    private String email;
    @NotBlank(message = "login не должен быть пустым")
    private String login;
    private String name;
    @CheckDateUserBirthday(message = "День рождение не может в будущем")
    private LocalDate birthday;
}
