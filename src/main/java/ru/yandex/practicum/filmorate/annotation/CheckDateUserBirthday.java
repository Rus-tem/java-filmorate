package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import ru.yandex.practicum.filmorate.validator.UserDateValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserDateValidator.class)
public @interface CheckDateUserBirthday {

    String message() default "Дата должна быть не позже {value}";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String value() default "LocalDate.now";
}

