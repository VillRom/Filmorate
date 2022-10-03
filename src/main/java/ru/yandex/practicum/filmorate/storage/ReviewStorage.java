package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review add(Review review);

    Review update(Review review);

    void delete(int id);

    Review get(int id);

    List<Review> getAll(int count);

    List<Review> getForFilm(int id, int count);
}
