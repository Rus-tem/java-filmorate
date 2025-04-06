package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.CheckDateFilm;

import java.time.LocalDate;

public class FilmDateValidator implements ConstraintValidator<CheckDateFilm, LocalDate> {
    private LocalDate minimumDate;

    @Override
    public void initialize(CheckDateFilm constraintAnnotation) {
        minimumDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isBefore(minimumDate);
    }
}