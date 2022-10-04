package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(long idFilm);

    List<Film> getFilms();

    Film getFilmById(long idFilm);

    void putLikeToFilm(long idFilm, long idUser);

    void deleteLikeToFilm(long idFilm, long idUser);

    List<Film> getSortedFilmsByDirectorOrderYear(long idDirector);

    List<Film> getSortedFilmsByDirectorOrderLikes(long idDirector);

    List<Film> search(String title, String director);
}
