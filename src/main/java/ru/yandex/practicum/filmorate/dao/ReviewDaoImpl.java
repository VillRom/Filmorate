package ru.yandex.practicum.filmorate.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class ReviewDaoImpl implements ReviewDao{

    private final JdbcTemplate jdbcTemplate;

    public ReviewDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review add(Review review) {
        String sqlQuery = "INSERT INTO REVIEWS (CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"review_id"});
            statement.setString(1, review.getContent());
            statement.setBoolean(2, review.getIsPositive());
            statement.setInt(3, review.getUserId());
            statement.setInt(4, review.getFilmId());
            statement.setInt(5, review.getUseful());
            return statement;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().intValue());

        return review;
    }

    @Override
    public Review update(Review review) {
        String sqlQuery = "UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ?" +
                "WHERE REVIEW_ID = ?";
        int result = jdbcTemplate.update(sqlQuery, review.getContent(), review.getIsPositive(),
                review.getReviewId());
        if (result != 1) {
            throw new NotFoundException("Отзыв по ID " + review.getReviewId() + " не найден!");
        }

        return review;
    }

    @Override
    public int delete(int id) {
        String sqlQuery = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";

        return jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Review get(int id) {
        String sqlQuery = "SELECT * FROM REVIEWS WHERE REVIEW_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, id);
        } catch (DataAccessException e) {
            throw new NotFoundException("Отзыв по ID " + id + " не найден!");
        }
    }

    @Override
    public List<Review> getAll(int count) {
        String sqlQuery = "SELECT * FROM REVIEWS ORDER BY USEFUL DESC LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, count);
    }

    @Override
    public List<Review> getForFilm(int id, int count) {
        String sqlQuery = "SELECT * FROM REVIEWS WHERE FILM_ID = ? ORDER BY USEFUL DESC LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, id, count);
    }

    @Override
    public void addLike(int id) {
        String sqlQuery = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = ?";
        int result = jdbcTemplate.update(sqlQuery, id);
        if (result != 1) {
            throw new NotFoundException("Отзыв по ID " + id + " не найден!");
        }

    }

    @Override
    public void removeLike(int id) {
        String sqlQuery = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = ?";
        int result = jdbcTemplate.update(sqlQuery, id);
        if (result != 1) {
            throw new NotFoundException("Отзыв по ID " + id + " не найден!");
        }

    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        Review review = new Review(resultSet.getString("content"),
                resultSet.getInt("user_id"),
                resultSet.getInt("film_id"));
        review.setReviewId(resultSet.getInt("review_id"));
        review.setUseful(resultSet.getInt("useful"));
        review.setIsPositive(resultSet.getBoolean("is_positive"));

        return review;
    }
}
