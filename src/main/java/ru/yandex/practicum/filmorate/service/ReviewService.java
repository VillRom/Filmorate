package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.ReviewLikesStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final ReviewLikesStorage reviewLikesStorage;
    private final UserStorage users;
    private final FilmStorage films;

    public ReviewService(ReviewStorage reviewStorage, ReviewLikesStorage reviewLikesStorage, UserStorage users, FilmStorage films) {
        this.reviewStorage = reviewStorage;
        this.reviewLikesStorage = reviewLikesStorage;
        this.users = users;
        this.films = films;
    }

    public Review add(Review review) {
        if (users.getUserFromId(review.getUserId()) == null) {
            throw new NotFoundException("Пользователь по ID " + review.getUserId() + " не найден!");
        }
        if (films.getFilmById(review.getFilmId()) == null) {
            throw new NotFoundException("Фильм по ID " + review.getFilmId() + " не найден!");
        }
        return reviewStorage.add(review);
    }

    public Review update(Review review) {
        if (users.getUserFromId(review.getUserId()) == null) {
            throw new NotFoundException("Пользователь по ID " + review.getUserId() + " не найден!");
        }
        if (films.getFilmById(review.getFilmId()) == null) {
            throw new NotFoundException("Фильм по ID " + review.getFilmId() + " не найден!");
        }
        return reviewStorage.update(review);
    }

    public void delete(int id) {
        reviewStorage.delete(id);
    }

    public Review get(int id) {
        return reviewStorage.get(id);
    }

    public List<Review> getForFilm(int id, int count) {
        if (id == 0) {
            return reviewStorage.getAll(count);
        }
        return reviewStorage.getForFilm(id, count);
    }

    public void addLike(int reviewId, int userId) {
        reviewLikesStorage.addLike(reviewId, userId);
    }

    public void addDislike(int reviewId, int userId) {
        reviewLikesStorage.addDislike(reviewId, userId);
    }

    public void removeLike(int reviewId, int userId) {
        reviewLikesStorage.removeLike(reviewId, userId);
    }

    public void removeDislike(int reviewId, int userId) {
        reviewLikesStorage.removeDislike(reviewId, userId);
    }
}
