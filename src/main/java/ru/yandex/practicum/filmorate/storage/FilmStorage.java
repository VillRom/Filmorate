package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    public Film addFilm(Film film);
    public Film updateFilm(Film film);
    public void deleteFilm(long idFilm);
    public List<Film> getAllFilms();
    public Film getFilmById(long idFilm);
    public void putLikeToFilm(long idFilm, long idUser);
    public void deleteLikeToFilm(long idFilm, long idUser);
    public List<Film> getSortedFilmsByDirectorOrderYear(long idDirector);
    public List<Film> getSortedFilmsByDirectorOrderLikes(long idDirector);
    public List<Film> getSortedFilmsOrderCount(int count);
    public List<Film> getSortByYearFilmsOrderCount(int count, int year);
    public List<Film> getSortByGenreFilmsOrderCount(int count, int idGenre);
    public List<Film> getSortByGenreAndYearFilmsOrderCount(int count, int year, int idGenre);
}
