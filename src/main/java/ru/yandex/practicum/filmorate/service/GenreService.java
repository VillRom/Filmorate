package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@Service
public class GenreService {
    private final GenreStorage genreStorage;

    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getAllGenre() {
        return genreStorage.getAllGenre();
    }

    public Genre getGenre(int id) throws AccountNotFoundException {
        if (id <= 0) {
            throw new AccountNotFoundException();
        }
        return genreStorage.getGenreById(id);
    }
}
