package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    public Film addFilm(Film film);
    public Film updateFilm(Film film);
    public void deleteFilm(long idFilm);
    public List<Film> getFilms();
    public Film getFilmFromId(long idFilm);
    public void putLikeToFilm(long idFilm, long idUser);
}
