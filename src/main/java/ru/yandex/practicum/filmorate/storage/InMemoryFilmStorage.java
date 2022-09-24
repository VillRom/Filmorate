package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("FilmInMemory")
public class InMemoryFilmStorage implements FilmStorage {
    private long filmId = 1;

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        film.setId(filmId);
        filmId++;
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        log.info("Обновлен фильм film: {}", films.get(film.getId()));
        return film;
    }

    @Override
    public void deleteFilm(long idFilm) {

    }
    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }
    @Override
    public Film getFilmFromId(long idFilm) {
        return films.get(idFilm);
    }

    @Override
    public void putLikeToFilm(long idFilm, long idUser) {
        films.get(idFilm).getLikes().add(idUser);
    }
}
