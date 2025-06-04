package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

@Repository
@Primary
public class ReviewDbStorage extends BaseStorage implements ReviewStorage {

    public ReviewDbStorage(JdbcTemplate jdbc, @Qualifier("reviewMapper") RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    private static final String FIND_ALL_REVIEW = """
            SELECT r.*, sum(CASE WHEN rl.likes IS NULL THEN 0 ELSE rl.likes END) AS useful
            FROM REVIEWS AS r
            LEFT JOIN REVIEW_LIKES AS rl ON r.review_id = rl.review_id
            GROUP BY r.review_id
            ORDER BY useful DESC
            LIMIT ?;""";

    private static final String FIND_ALL_REVIEW_BY_ID_FILM = """
            SELECT r.*, sum(CASE WHEN rl.likes IS NULL THEN 0 ELSE rl.likes END) AS useful
            FROM REVIEWS AS r
            LEFT JOIN REVIEW_LIKES AS rl ON r.review_id = rl.review_id
            WHERE r.film_id =?
            GROUP BY r.review_id
            ORDER BY useful DESC
            LIMIT ?;""";

    private static final String CREATE_NEW_REVIEW = """
            INSERT INTO reviews(content, is_positive, user_id, film_id) VALUES(?,?,?,?);""";

    private static final String FIND_REVIEW_BY_ID = """
            SELECT r.*, sum(CASE WHEN rl.likes IS NULL THEN 0 ELSE rl.likes END) AS useful
            FROM REVIEWS AS r
            LEFT JOIN REVIEW_LIKES AS rl ON r.review_id = rl.review_id
            WHERE r.review_id =?
            GROUP BY r.review_id;""";
    private static final String UPDATE_REVIEW = """
            UPDATE REVIEWS
            SET content = ?, is_positive = ?
            WHERE review_id = ?;""";

    private static final String ADD_LIKE_DISLIKE_REVIEW = "MERGE INTO REVIEW_LIKES (review_id, user_id, likes) KEY (review_id, user_id) VALUES(?,?,?);";


    private static final String DELETE_LIKE_DISLIKE_REVIEW = "DELETE REVIEW_LIKES WHERE review_id=? AND user_id=? AND likes=?;";

    private static final String DELETE_REVIEW_BY_ID = "DELETE FROM REVIEWS WHERE review_id =?";
    private static final String DELETE_REVIEW_LIKES_BY_ID = "DELETE FROM REVIEW_LIKES WHERE review_id =?";


    //получение списка всех отзывов
    @Override
    public Collection<Review> getAllReview(int count) {
        return findMany(FIND_ALL_REVIEW, count);
    }

    //получение всех отзывов по id фильма
    public Collection<Review> getAllReviewByIdFilm(long filmId, int count) {
        return findMany(FIND_ALL_REVIEW_BY_ID_FILM, filmId, count);
    }


    //получение отзыв по id
    public Review getReviewById(long reviewId) {
        Optional<Review> review = findOne(FIND_REVIEW_BY_ID, reviewId);
        if (review.isPresent()) {
            return review.get();
        }
        throw new NotFoundException("нет такого отзыва");
    }

    //создание отзыва
    @Override
    public Review create(Review review) {
        long id = insert(CREATE_NEW_REVIEW, review.getContent(), review.getIsPositive(), review.getUserId(), review.getFilmId());
        review.setReviewId(id);
        return review;
    }

    //обновление отзыва
    @Override
    public Review update(Review newReview) {
        update(UPDATE_REVIEW, newReview.getContent(), newReview.getIsPositive(), newReview.getReviewId());
        Review review = getReviewById(newReview.getReviewId());
        return review;
    }

    //удаление отзыва
    public void delete(Long reviewId) {
        jdbc.update(DELETE_REVIEW_LIKES_BY_ID, reviewId);
        jdbc.update(DELETE_REVIEW_BY_ID, reviewId);
    }

    //ставим лайк/дизлайк отзыву
    public void addLikeDislikeReviewId(Long reviewId, Long userId, Boolean like) {
        if (like) {
            jdbc.update(ADD_LIKE_DISLIKE_REVIEW, reviewId, userId, 1);
        } else {
            jdbc.update(ADD_LIKE_DISLIKE_REVIEW, reviewId, userId, -1);
        }
    }

    //удаляем лайк/дизлайк отзыва
    public void deleteLikeDislikeReviewId(Long reviewId, Long userId, Boolean like) {
        if (like) {
            jdbc.update(DELETE_LIKE_DISLIKE_REVIEW, reviewId, userId, 1);
        } else {
            jdbc.update(DELETE_LIKE_DISLIKE_REVIEW, reviewId, userId, -1);
        }
    }

}
