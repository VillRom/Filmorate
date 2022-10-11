package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final ReviewLikesStorage reviewLikesStorage;
    private final UserStorage users;
    private final FilmStorage films;
    private final EventFeedStorage eventDb;


    public Review add(Review review) {
        if (users.getUserFromId(review.getUserId()) == null) {
            throw new NotFoundException("Пользователь по ID " + review.getUserId() + " не найден!");
        }
        if (films.getFilmById(review.getFilmId()) == null) {
            throw new NotFoundException("Фильм по ID " + review.getFilmId() + " не найден!");
        }
        Review addReview = reviewStorage.add(review);
        eventDb.addEvent(addReview.getUserId(), "REVIEW", "ADD", addReview.getReviewId());
        log.info("Добавлен отзыв к фильму c id " + addReview.getFilmId() + " от пользователя с id "
                + addReview.getUserId());
        return addReview;
    }

    public Review update(Review review) {
        if (users.getUserFromId(review.getUserId()) == null) {
            throw new NotFoundException("Пользователь по ID " + review.getUserId() + " не найден!");
        }
        if (films.getFilmById(review.getFilmId()) == null) {
            throw new NotFoundException("Фильм по ID " + review.getFilmId() + " не найден!");
        }
        Review updateReview = reviewStorage.update(review);
        eventDb.addEvent(updateReview.getUserId(), "REVIEW", "UPDATE", review.getReviewId());
        log.info("Обновлен отзыв к фильму c id " + updateReview.getFilmId() + " от пользователя с id "
                + updateReview.getUserId());
        return updateReview;
    }

    public void delete(int id) {
        long userId = reviewStorage.delete(id);
        log.info("Удален отзыв с идентификатором " + id);
        eventDb.addEvent(userId, "REVIEW", "REMOVE", id);
    }

    public Review get(int id) {
        try {
            return reviewStorage.get(id);
        } catch (DataAccessException e) {
            throw new NotFoundException("Отзыв по ID " + id + " не найден!");
        }
    }

    public List<Review> getForFilm(int id, int count) {
        if (id == 0) {
            return reviewStorage.getAll(count);
        }
        return reviewStorage.getForFilm(id, count);
    }

    public void addLike(int reviewId, int userId) {
        try {
            reviewLikesStorage.addLike(reviewId, userId);
            log.info("Добавлен лайк к отзыву с id " + reviewId + " от пользователя с id " + userId);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("передан неверный идентификатор!");
        }
    }

    public void addDislike(int reviewId, int userId) {
        try {
            reviewLikesStorage.addDislike(reviewId, userId);
            log.info("Добавлен дизлайк к отзыву с id " + reviewId + " от пользователя с id " + userId);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("передан неверный идентификатор!");
        }
    }

    public void removeLike(int reviewId, int userId) {
        try {
            reviewLikesStorage.removeLike(reviewId, userId);
            log.info("Удален лайк к отзыву с id " + reviewId + " от пользователя с id " + userId);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("передан неверный идентификатор!");
        }
    }

    public void removeDislike(int reviewId, int userId) {
        try {
            reviewLikesStorage.removeDislike(reviewId, userId);
            log.info("Удален дизлайк к отзыву с id " + reviewId + " от пользователя с id " + userId);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("передан неверный идентификатор!");
        }
    }
}
