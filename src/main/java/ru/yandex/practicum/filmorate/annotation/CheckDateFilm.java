package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import ru.yandex.practicum.filmorate.validator.FilmDateValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FilmDateValidator.class)
public @interface CheckDateFilm {

    String message() default "Дата должна быть не раньше {value}";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String value() default "1895-12-28";
}




