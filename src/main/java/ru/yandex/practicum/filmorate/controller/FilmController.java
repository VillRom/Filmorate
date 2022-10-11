package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}")
    public Film deleteFilmById(@PathVariable long id) {
        return filmService.deleteFilm(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getSortedFilms(@RequestParam(defaultValue = "10") Integer count,
                                           @RequestParam(required = false) Integer genreId,
                                           @RequestParam(required = false) Integer year) {
        return filmService.getSortedFilmsCount(count, year, genreId);
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable long id, @PathVariable long userId) {
        filmService.deleteLike(id, userId);
        return filmService.getFilmById(id);
    }
    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsByDirectorOrder(@PathVariable long directorId,
                                                    @RequestParam String sortBy) {
        return filmService.getSortedFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> search(@RequestParam String query, @RequestParam String by) {
        return filmService.search(query, by);
    }

    @GetMapping("/common")
    public List<Film> findCommon(@RequestParam int userId, @RequestParam int friendId) {

        return filmService.findCommon(userId, friendId);
    }
}
