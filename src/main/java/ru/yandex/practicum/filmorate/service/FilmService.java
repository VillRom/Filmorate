package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public FilmService(@Qualifier("FilmDb") FilmStorage filmStorage) {
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
        if (filmStorage.getFilmById(film.getId()) != null) {
            validation.validationFilm(film);
            log.info("Обновлен фильм film: {}", filmStorage.getFilmById(film.getId()));
            return filmStorage.updateFilm(film);
        } else {
            throw new AccountNotFoundException();
        }
    }

    public Film getFilmById(long idFilm) throws AccountNotFoundException {
        if (filmStorage.getFilmById(idFilm) == null || idFilm <= 0) {
            throw new AccountNotFoundException();
        }
        return filmStorage.getFilmById(idFilm);
    }

    public void addLike(long id, long userId) {
        filmStorage.putLikeToFilm(id, userId);
        log.info("Добавлен лайк к фильму " + filmStorage.getFilmById(id));
    }

    public void deleteLike(long id, long userId) throws AccountNotFoundException {
        if (userId <= 0) {
            throw new AccountNotFoundException();
        }
        filmStorage.getFilmById(id).getLikes().remove(userId);
        log.info("Удален лайк пользователя с id-" + userId + " к фильму " + filmStorage.getFilmById(id));
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

    public Film deleteFilm( long idFilm) throws AccountNotFoundException {
        if (filmStorage.getFilmById(idFilm) == null || idFilm <= 0) {
            throw new AccountNotFoundException();
        }
        Film film = filmStorage.getFilmById(idFilm);
        filmStorage.deleteFilm(idFilm);
        log.info("Удален фильм " + film);
        return film;
    }
}
