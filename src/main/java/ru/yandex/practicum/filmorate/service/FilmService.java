package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import validation.Validation;

import javax.security.auth.login.AccountNotFoundException;
import java.util.*;
@Slf4j
@Service
public class FilmService {
    private final Validation validation;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        validation = new Validation();
        this.filmStorage = filmStorage;
    }

    public Film createFilm(Film film) {
        validation.validationFilm(film);
        filmStorage.addFilm(film);
        log.info("Добавлен film: {}", film.toString());
        return film;
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }


    public Film updateFilm(Film film) throws AccountNotFoundException {
        if (filmStorage.getFilmFromId(film.getId()) != null) {
            validation.validationFilm(film);
            filmStorage.updateFilm(film);
            log.info("Обновлен фильм film: {}", filmStorage.getFilmFromId(film.getId()));
        } else {
            throw new AccountNotFoundException();
        }
        return film;
    }

    public Film getFilmFromId(long idFilm) throws AccountNotFoundException {
        if (filmStorage.getFilmFromId(idFilm) == null || idFilm <= 0) {
            throw new AccountNotFoundException();
        }
        return filmStorage.getFilmFromId(idFilm);
    }

    public void addLike(long id, long userId) {
        filmStorage.getFilmFromId(id).getLikes().add(userId);
        log.info("Добавлен лайк к фильму " + filmStorage.getFilmFromId(id));
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
