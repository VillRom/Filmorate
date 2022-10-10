package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    // Добавляем фильм
    Film addFilm(Film film);

    // Обновляем информацию о фильме
    Film updateFilm(Film film);

    // Удаляем фильм по идентификатору
    void deleteFilm(long idFilm);

    // Возвращаем список всех фильмов
    List<Film> getAllFilms();

    // Возвращаем фильм по идентификатору
    Film getFilmById(long idFilm);

    // Добаляем лайк к фильму idFilm от пользователя idUser
    void putLikeToFilm(long idFilm, long idUser);

    // Удаляем лайк к фильму idFilm от пользователя idUser
    void deleteLikeToFilm(long idFilm, long idUser);

    // Возвращаем список фильмов режиссера idDirector, отсортированный по году
    List<Film> getSortedFilmsByDirectorOrderYear(long idDirector);

    // Возвращаем список фильмов режиссера idDirector, отсортированный по лайкам
    List<Film> getSortedFilmsByDirectorOrderLikes(long idDirector);

    // Осуществляем поиск фильмов по названию или имени режиссера. Возвращаем список фильмов
    List<Film> search(String title, String director);

    // Возвращаем отсортированный по лайкам список фильмов в кол-ве count
    List<Film> getSortedFilmsOrderCount(int count);

    // Возвращаем отсортированный по лайкам список фильмов с фильтрацией по жанру или году
    List<Film> getSortByGenreOrYearFilmsOrderCount(int count, Integer year, Integer idGenre);

    // Возвращаем отсортированный по лайкам список фильмов с фильтрацией по жанру или году
    List<Film> getSortByGenreAndYearFilmsOrderCount(int count, int year, int idGenre);

    // Возвращаем отсортированный по лайкам список фильмов общих с другом пользователя
    List<Film> findCommon(int userId, int friendId);
}
