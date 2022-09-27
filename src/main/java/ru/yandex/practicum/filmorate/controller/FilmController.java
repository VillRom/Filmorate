package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private long id;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public ResponseEntity<List<Film>> getFilms() {
        return ResponseEntity.ok(filmService.getFilms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(filmService.getFilmById(id));
        } catch (AccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Film> deleteFilmById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(filmService.deleteFilm(id));
        } catch (AccountNotFoundException e) {
            throw new RuntimeException(e);
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
        try {
            return ResponseEntity.ok(filmService.createFilm(film));
        } catch (ValidationException e) {
            log.warn("Исключение! ValidationException Film: {}", e.getMessage());
            return ResponseEntity.badRequest().body(film);
        }
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        try {
            return ResponseEntity.ok(filmService.updateFilm(film));
        } catch (ValidationException e) {
            log.warn("Исключение! ValidationException Film: {}", e.getMessage());
            return ResponseEntity.badRequest().body(film);
        } catch (AccountNotFoundException e) {
            log.warn("Исключение! AccountNotFoundException Film: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable long id, @PathVariable long userId)
            throws AccountNotFoundException {
        filmService.addLike(id, userId);
        return ResponseEntity.ok(filmService.getFilmById(id));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> deleteLike(@PathVariable long id, @PathVariable long userId)
            throws AccountNotFoundException {
        try {
            filmService.deleteLike(id, userId);
        } catch (AccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(filmService.getFilmById(id));
    }
}
