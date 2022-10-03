package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

@Component
public class ReviewLikesDbStorage implements ReviewLikesStorage {

    private final JdbcTemplate jdbcTemplate;

    public ReviewLikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(int reviewId, int userId) {
        try {
            String sqlQuery = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlQuery, reviewId, userId, 1);
            String queryReview = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
            jdbcTemplate.update(queryReview, reviewId);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("передан неверный идентификатор!");
        }
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        try {
            String sqlQuery = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlQuery, reviewId, userId, -1);
            String queryReview = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";
            jdbcTemplate.update(queryReview, reviewId);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("передан неверный идентификатор!");
        }
    }

    @Override
    public void removeLike(int reviewId, int userId) {
        String sqlQuery = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = 1";
        int result = jdbcTemplate.update(sqlQuery, reviewId, userId);
        if (result != 1) {
            throw new NotFoundException("передан неверный идентификатор!");
        }
        String queryReview = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";
        jdbcTemplate.update(queryReview, reviewId);
    }

    @Override
    public void removeDislike(int reviewId, int userId) {
        String sqlQuery = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = -1";
        int result = jdbcTemplate.update(sqlQuery, reviewId, userId);
        if (result != 1) {
            throw new NotFoundException("передан неверный идентификатор!");
        }
        String queryReview = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
        jdbcTemplate.update(queryReview, reviewId);
    }
}
