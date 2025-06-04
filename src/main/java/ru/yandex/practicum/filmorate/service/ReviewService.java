package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewDbStorage;

import java.util.Collection;

@Slf4j
@Service
public class ReviewService {

    private final ReviewDbStorage reviewDbStorage;
    private final UserService userService;

    public ReviewService(ReviewDbStorage reviewDbStorage, UserService userService) {
        this.reviewDbStorage = reviewDbStorage;
        this.userService = userService;
    }

    // возращаем список отзывов
    public Collection<Review> getAllReviewById(Long filmId, int count) {
        if (filmId == null || filmId == 0) {
            return reviewDbStorage.getAllReview(count);
        } else {
            return reviewDbStorage.getAllReviewByIdFilm(filmId, count);
        }
    }

    //получение отзыв по id
    public Review getReviewById(long reviewId) {
        return reviewDbStorage.getReviewById(reviewId);
    }

    //добавляем отзыв
    public Review createReview(Review review) {
        if (review.getFilmId() <= 0 || review.getUserId() <= 0) {
            throw new NotFoundException("значение должно быть > 0");
        }
        if (review.getContent() == null) {
            throw new NullPointerException("не должен быть null");
        }
        if (review.getContent().isBlank() || review.getContent().isEmpty()) {
            throw new NullPointerException("не должен быть пустым");
        }
        if (review.getIsPositive() == null) {
            throw new NullPointerException("не должен быть null");
        }

        userService.addFeed(review.getUserId(), "REVIEW", "ADD", review.getReviewId());
        return reviewDbStorage.create(review);
    }

    //удаление отзыва
    public void deleteReview(Long reviewId) {
        Review review = getReviewById(reviewId);
        userService.addFeed(review.getUserId(), "REVIEW", "REMOVE", reviewId);
        reviewDbStorage.delete(reviewId);
    }

    // Обновление отзыва
    public Review updateReview(Review newReview) {
        if (newReview.getFilmId() <= 0 || newReview.getUserId() <= 0) {
            throw new NotFoundException("значение должно быть > 0");
        }
        if (newReview.getContent() == null) {
            throw new NullPointerException("не должен быть null");
        }
        if (newReview.getContent().isBlank() || newReview.getContent().isEmpty()) {
            throw new NullPointerException("не должен быть пустым");
        }
        if (newReview.getIsPositive() == null) {
            throw new NullPointerException("не должен быть null");
        }
        userService.addFeed(newReview.getUserId(), "REVIEW", "UPDATE", newReview.getReviewId());

        return reviewDbStorage.update(newReview);
    }


    //ставим лайк/дизлайк отзыву
    public void addLikeDislikeReviewId(Long reviewId, Long userId, boolean like) {

        reviewDbStorage.addLikeDislikeReviewId(reviewId, userId, like);
    }

    //удаляем лайк/дизлайк отзыва
    public void deleteLikeDislikeReviewId(Long reviewId, Long userId, boolean like) {

        reviewDbStorage.deleteLikeDislikeReviewId(reviewId, userId, like);
    }


}
