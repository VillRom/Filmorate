package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDao {

    Review add(Review review);

    Review update(Review review);

    int delete(int id);

    Review get(int id);

    List<Review> getAll(int count);

    List<Review> getForFilm(int id, int count);

    void addLike(int id);

    void removeLike(int id);


}
