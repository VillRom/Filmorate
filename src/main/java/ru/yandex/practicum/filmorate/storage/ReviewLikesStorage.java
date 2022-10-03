package ru.yandex.practicum.filmorate.storage;

public interface ReviewLikesStorage {

    void addLike(int reviewId, int userId);

    void addDislike(int reviewId, int userId);

    void removeLike(int reviewId, int userId);

    void removeDislike(int reviewId, int userId);
}
