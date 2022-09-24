package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;


public interface GenreStorage {
    public List<Genre> getAllGenre();
    public Genre getGenreFromId(Integer id) throws AccountNotFoundException;
}
