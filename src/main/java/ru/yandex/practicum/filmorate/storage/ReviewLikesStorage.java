package ru.yandex.practicum.filmorate.storage;

public interface ReviewLikesStorage {

    // Добавляем лайк к отзыву reviewId от пользователя userId
    void addLike(int reviewId, int userId);

    // Добавляем дизлайк к отзыву reviewId от пользователя userId
    void addDislike(int reviewId, int userId);

    // Удаляем лайк к отзыву reviewId от пользователя userId
    void removeLike(int reviewId, int userId);

    // Удаляем дизлайк к отзыву reviewId от пользователя userId
    void removeDislike(int reviewId, int userId);
}
