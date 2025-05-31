package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;


    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    // Добавление отзыва
    @PostMapping()
    public Review createReview(@Valid @RequestBody Review review) {
        return reviewService.createReview(review);
    }

    //получение отзыва по id

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    //получение всех отзывов
    @GetMapping
    public Collection<Review> getAllReview(@RequestParam(required = false) Long filmId,
                                           @RequestParam(required = false, defaultValue = "10") int count) {

        return reviewService.getAllReviewById(filmId, count);
    }

    //удаление отзыва
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }

    // Обновление отзыва

    @PutMapping
    public Review updateReview(@RequestBody Review newReview) {
        return reviewService.updateReview(newReview);
    }

    //ставим лайк отзыву
    @PutMapping("/{id}/like/{userId}")
    public void addLikeReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addLikeDislikeReviewId(id, userId, true);
    }

    //ставим дизлайк отзыву
    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addLikeDislikeReviewId(id, userId, false);
    }

    //удаляем лайк отзыва
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.deleteLikeDislikeReviewId(id, userId, true);
    }

    //удаляем дизлайк отзыва
    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.deleteLikeDislikeReviewId(id, userId, false);
    }
}



