package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    public FilmController(InMemoryFilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }
    @GetMapping
    public ResponseEntity<List<Film>> getFilms() {
        return ResponseEntity.ok(filmStorage.getFilms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmFromId(@PathVariable long id) {
        try {
            return ResponseEntity.ok(filmStorage.getFilmFromId(id));
        } catch (AccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<List> getSortedFilms(@RequestParam(required = false) Integer count) {
        if(count != null) {
            return ResponseEntity.ok(filmService.getSortedFilms(count));
        } else {
            return ResponseEntity.ok(filmService.getSortedFilms(10));
        }
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        return filmStorage.createFilm(film);
    }


    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) throws ValidationException {
        return filmStorage.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable long id, @PathVariable long userId) throws AccountNotFoundException {
        filmService.addLike(id, userId);
        return ResponseEntity.ok(filmStorage.getFilmFromId(id));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> deleteLike(@PathVariable long id, @PathVariable long userId) throws AccountNotFoundException {
        try {
            filmService.deleteLike(id, userId);
        } catch (AccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(filmStorage.getFilmFromId(id));
    }


}
