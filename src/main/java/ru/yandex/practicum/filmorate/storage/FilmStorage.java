package ru.yandex.practicum.filmorate.storage;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

public interface FilmStorage {
    public ResponseEntity<Film> createFilm(Film film);
    public ResponseEntity<Film> updateFilm(Film film);
    public void deleteFilm(long idFilm);
    public List<Film> getFilms();
    public Film getFilmFromId(long idFilm) throws AccountNotFoundException;
}
