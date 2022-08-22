package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import validation.Validation;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage{

    private final Validation validation = new Validation();

    private long filmId = 1;

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public ResponseEntity<Film> createFilm(Film film) {
        try {
            validation.validationFilm(film);
            film.setId(filmId);
            filmId++;
            films.put(film.getId(), film);
            log.info("Добавлен film: {}", film.toString());
        } catch (ValidationException e) {
            log.warn("Исключение! ValidationException Film: {}", e.getMessage());
            return ResponseEntity.badRequest().body(film);
        }
        return ResponseEntity.ok(film);
    }

    @Override
    public ResponseEntity<Film> updateFilm(Film film) {
        try {
            if (films.containsKey(film.getId())) {
                validation.validationFilm(film);
                films.put(film.getId(), film);
                log.info("Обновлен фильм film: {}", films.get(film.getId()));
            } else {
                throw new AccountNotFoundException();
            }
        } catch (ValidationException e) {
            log.warn("Исключение! ValidationException Film: {}", e.getMessage());
            return ResponseEntity.badRequest().body(film);
        } catch (AccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(film);
    }

    @Override
    public void deleteFilm(long idFilm) {

    }
    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }
    @Override
    public Film getFilmFromId(long idFilm) throws AccountNotFoundException {
        if (!films.containsKey(idFilm) || idFilm <= 0) {
            throw new AccountNotFoundException();
        }
        return films.get(idFilm);
    }
}
