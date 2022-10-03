package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewDao reviewDao;
    private final UserStorage users;
    private final FilmStorage films;

    public ReviewService(ReviewDao reviewDao, UserStorage users, FilmStorage films) {
        this.reviewDao = reviewDao;
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
        return reviewDao.add(review);
    }

    public Review update(Review review) {
        if (users.getUserFromId(review.getUserId()) == null) {
            throw new NotFoundException("Пользователь по ID " + review.getUserId() + " не найден!");
        }
        if (films.getFilmById(review.getFilmId()) == null) {
            throw new NotFoundException("Фильм по ID " + review.getFilmId() + " не найден!");
        }
        return reviewDao.update(review);
    }

    public void delete(int id) {
        reviewDao.delete(id);
    }

    public Review get(int id) {
        return reviewDao.get(id);
    }

    public List<Review> getForFilm(int id, int count) {
        if (id == 0) {
            return reviewDao.getAll(count);
        }
        return reviewDao.getForFilm(id, count);
    }

    public void addLike(int id) {
        reviewDao.addLike(id);
    }

    public void removeLike(int id) {
        reviewDao.removeLike(id);
    }
}
