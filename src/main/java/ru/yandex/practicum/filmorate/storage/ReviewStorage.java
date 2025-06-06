package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {

    Collection<Review> getAllReview(int count);

    Review create(Review review);

    Review update(Review newReview);
}
