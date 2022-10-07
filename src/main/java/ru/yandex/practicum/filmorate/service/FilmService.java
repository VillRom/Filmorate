package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.AccountNotFound;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.Event;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import validation.Validation;

import java.util.*;

@Slf4j
@Service
public class FilmService {
    private final Validation validation;
    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;
    private final Event event;

    @Autowired
    public FilmService(@Qualifier("FilmDb") FilmStorage filmStorage, DirectorStorage directorStorage, Event event) {
        this.event = event;
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
        return filmStorage.getAllFilms();
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
        event.addEvent(userId, "LIKE", "ADD", id);
        log.info("Добавлен лайк к фильму " + filmStorage.getFilmById(id));
    }

    public void deleteLike(long id, long userId) throws AccountNotFound {
        if (userId <= 0) {
            throw new AccountNotFound("Пользователь с id = " + userId + " не найден");
        }
        filmStorage.deleteLikeToFilm(id, userId);
        event.addEvent(userId, "LIKE", "REMOVE", id);
        log.info("Удален лайк пользователя с id-" + userId + " к фильму " + filmStorage.getFilmById(id));
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
            searched = filmStorage.search(newQuery, null);
        } else if (param.equals("director")) {
            searched = filmStorage.search(null, newQuery);
        } else {
            searched = filmStorage.search(newQuery, newQuery);
        }
        searched.sort((o1, o2) -> o2.getLikes().size() - o1.getLikes().size());

        return searched;
    }

    public List<Film> getSortedFilmsCount(int count, Integer year, Integer genreId) {
        if (year != null & genreId != null) {
            return filmStorage.getSortByGenreAndYearFilmsOrderCount(count, year, genreId);
        } else if (year == null & genreId == null){
            return filmStorage.getSortedFilmsOrderCount(count);
        } else {
            return filmStorage.getSortByGenreOrYearFilmsOrderCount(count, year, genreId);
        }
    }

    public List<Film> findCommon(int userId, int friendId) {
        List<Film> common = filmStorage.findCommon(userId, friendId);
        common.sort((o1, o2) -> o2.getLikes().size() - o1.getLikes().size());

        return common;
    }
}
