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

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public ResponseEntity<List<Film>> getFilms() {
        return ResponseEntity.ok(new ArrayList<>(films.values()));
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        try {
            validation.validationFilm(film);
            films.put(film.getId(), film);
            log.info("Добавлен film: {}", film.toString());
        }catch (ValidationException e) {
            log.warn("Выпало исключение ValidationException Film: {}", e.getMessage());
            return ResponseEntity.badRequest().body(film);
        }
        return ResponseEntity.ok(film);
    }


    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) throws ValidationException {
        try {
            if (films.containsKey(film.getId())) {
                if (!films.get(film.getId()).getDescription().equals(film.getDescription())) {
                    films.get(film.getId()).setDescription(film.getDescription());
                } if (!films.get(film.getId()).getName().equals(film.getName())) {
                    films.get(film.getId()).setName(film.getName());
                } if (films.get(film.getId()).getDuration() != film.getDuration()) {
                    films.get(film.getId()).setDuration(film.getDuration());
                } if (!films.get(film.getId()).getReleaseDate().equals(film.getReleaseDate())) {
                    films.get(film.getId()).setReleaseDate(film.getReleaseDate());
                }
            } else {
                throw new AccountNotFoundException();
            }
        } catch (ValidationException e) {
            log.warn("Выпало исключение ValidationException Film: {}", e.getMessage());
            return ResponseEntity.badRequest().body(film);
        } catch (AccountNotFoundException e) {
            return ResponseEntity.internalServerError().body(film);
        }
        return ResponseEntity.ok(film);
    }
}
