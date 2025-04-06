package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.CheckDateUserBirthday;

import java.time.LocalDate;

public class UserDateValidator implements ConstraintValidator<CheckDateUserBirthday, LocalDate> {
    private LocalDate minimumDate;

    @Override
    public void initialize(CheckDateUserBirthday constraintAnnotation) {
        minimumDate = LocalDate.now();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isAfter(minimumDate);
    }

}
