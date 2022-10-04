package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.AccountNotFound;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import validation.Validation;

import java.util.*;

@Slf4j
@Service
public class FilmService {
    private final Validation validation;
    private final FilmStorage filmStorage;

    private final DirectorStorage directorStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDb") FilmStorage filmStorage, DirectorStorage directorStorage) {
        validation = new Validation();
        this.filmStorage = filmStorage;
        this.directorStorage = directorStorage;
    }

    public Film createFilm(Film film) {
        validation.validationFilm(film);
        filmStorage.addFilm(film);
        log.info("Добавлен film: {}", film);
        return film;
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }


    public Film updateFilm(Film film) throws AccountNotFound {
        if (filmStorage.getFilmById(film.getId()) != null) {
            validation.validationFilm(film);
            log.info("Обновлен фильм film: {}", filmStorage.getFilmById(film.getId()));
            return filmStorage.updateFilm(film);
        } else {
            throw new AccountNotFound("Фильм с id = " + film.getId() + " не найден");
        }
    }

    public Film getFilmById(long idFilm) throws AccountNotFound {
        if (filmStorage.getFilmById(idFilm) == null) {
            throw new AccountNotFound("Фильм с id = " + idFilm + " не найден");
        }
        return filmStorage.getFilmById(idFilm);
    }

    public void addLike(long id, long userId) {
        filmStorage.putLikeToFilm(id, userId);
        log.info("Добавлен лайк к фильму " + filmStorage.getFilmById(id));
    }

    public void deleteLike(long id, long userId) throws AccountNotFound {
        if (userId <= 0) {
            throw new AccountNotFound("Пользователь с id = " + userId + " не найден");
        }
        filmStorage.deleteLikeToFilm(id, userId);
        log.info("Удален лайк пользователя с id-" + userId + " к фильму " + filmStorage.getFilmById(id));
    }

    public List<Film> getSortedFilms(int count) {
        List<Film> sortedFilms = filmStorage.getFilms();
        sortedFilms.sort((o1, o2) -> o2.getLikes().size() - o1.getLikes().size());
        if (sortedFilms.size() < count) {
            return sortedFilms.subList(0, sortedFilms.size());
        } else {
            return sortedFilms.subList(0, count);
        }
    }

    public Film deleteFilm(long idFilm) throws AccountNotFound {
        if (filmStorage.getFilmById(idFilm) == null) {
            throw new AccountNotFound("Фильм с id = " + idFilm + " не найден");
        }
        Film film = filmStorage.getFilmById(idFilm);
        filmStorage.deleteFilm(idFilm);
        log.info("Удален фильм " + film);
        return film;
    }

    public List<Film> getSortedFilmsByDirector(long idDirector, String sort) throws AccountNotFound {
        if (directorStorage.getDirector(idDirector) == null) {
            throw new AccountNotFound("Режисер с id = " + idDirector + " не найден");
        }
        if (sort.equals("year")) {
            return filmStorage.getSortedFilmsByDirectorOrderYear(idDirector);
        } else if (sort.equals("likes")) {
            return filmStorage.getSortedFilmsByDirectorOrderLikes(idDirector);
        }
        return null;
    }

    public List<Film> search(String query, String param) {
        String newQuery = query.toLowerCase();
        List<Film> searched;
        if (param.equals("title")) {
            searched = filmStorage.searchByTitle(newQuery);
        } else if (param.equals("director")) {
            searched = filmStorage.searchByDirector(newQuery);
        } else {
            searched = filmStorage.searchByDirector(newQuery);
            searched.addAll(filmStorage.searchByTitle(newQuery));
        }
        searched.sort((o1, o2) -> o2.getLikes().size() - o1.getLikes().size());

        return searched;
    }
}
