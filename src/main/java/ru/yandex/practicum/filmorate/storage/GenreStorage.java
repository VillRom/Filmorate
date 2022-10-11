package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;


public interface GenreStorage {

    // Возвращает список всех жанров
    List<Genre> getAllGenre();

    // Возвращает жанр по индетификатору id
    Genre getGenreById(Integer id);
}
