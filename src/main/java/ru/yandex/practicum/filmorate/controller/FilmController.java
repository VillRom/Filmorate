package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import validation.Validation;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final Validation validation = new Validation();

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public ResponseEntity<List<Film>> getFilms() {
        return ResponseEntity.ok(new ArrayList<>(films.values()));
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        try {
            if (film.getId() == 0) {
                film.setId(1);
            }
            validation.validationFilm(film);
            films.put(film.getId(), film);
            log.info("Добавлен film: {}", film.toString());
        } catch (ValidationException e) {
            log.warn("Исключение! ValidationException Film: {}", e.getMessage());
            return ResponseEntity.badRequest().body(film);
        }
        return ResponseEntity.ok(film);
    }


    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) throws ValidationException {
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
            return ResponseEntity.internalServerError().body(film);
        }
        return ResponseEntity.ok(film);
    }
}
