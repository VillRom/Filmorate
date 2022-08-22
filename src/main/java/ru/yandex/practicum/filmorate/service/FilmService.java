package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.security.auth.login.AccountNotFoundException;
import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(long id, long userId) throws AccountNotFoundException {
        filmStorage.getFilmFromId(id).getLikes().add(userId);
    }

    public void deleteLike(long id, long userId) throws AccountNotFoundException {
        if (userId <= 0) {
            throw new AccountNotFoundException();
        }
        filmStorage.getFilmFromId(id).getLikes().remove(userId);
    }

    public List<Film> getSortedFilms(int count) {
        List<Film> sortedFilms = filmStorage.getFilms();
        sortedFilms.sort(new Comparator<Film>() {
            @Override
            public int compare(Film o1, Film o2) {
                return o2.getLikes().size() - o1.getLikes().size();
            }
        });
        if (sortedFilms.size() < count) {
            return sortedFilms.subList(0, sortedFilms.size());
        } else {
            return sortedFilms.subList(0, count);
        }
    }
}
