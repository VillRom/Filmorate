package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    // Добавляем отзыв к фильму
    Review add(Review review);

    // Обновляем отзыв
    Review update(Review review);

    // Удаляем отзыв к фильму
    Integer delete(int id);

    // Возвращаем отзыв по индетификатору
    Review get(int id);

    // Возвращаем список всех отзывов
    List<Review> getAll(int count);

    // Возвращаем список всех отзывов к фильму id в кол-ве count
    List<Review> getForFilm(int id, int count);
}
